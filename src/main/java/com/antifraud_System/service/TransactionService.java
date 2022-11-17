package com.antifraud_System.service;


import com.antifraud_System.entity.CardNumber_limit;
import com.antifraud_System.entity.FeedBack;
import com.antifraud_System.entity.Status;
import com.antifraud_System.entity.TransactionInfo;
import com.antifraud_System.exception.*;
import com.antifraud_System.reposiroty.CardNumber_limitRepository;
import com.antifraud_System.reposiroty.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TransactionService {

    @Autowired
    StolenCardService cardService;
    @Autowired
    SuspiciousIpService ipService;
    @Autowired
    TransactionRepository transactionRepo;

    @Autowired
    CardNumber_limitRepository cardNumber_limitRepo;


    private Long MAX_ALLOWED;
    private Long MAX_MANUAL;

    final static int MAX_DIFFERENCE_SOURCE = 3;

    @Transactional
    public Map<String, String> addTransaction(TransactionInfo transaction) {
        //renew limits of current card
        String cardNumber = transaction.getNumber();
        updateLimits(cardNumber);

        //check status
        transactionRepo.save(transaction);
        Status amountStatus = checkAmount(transaction.getAmount());
        Status ip = checkIp(transaction.getIp());
        Status number = checkNumber(transaction.getNumber());

        List<TransactionInfo> transactions = transactionRepo.findByNumberAndDateBetween(transaction.getNumber(),
                transaction.getDate().minusHours(1), transaction.getDate());
        Status ipCorrelation = checkIpCorrelation(transactions);
        Status regionCorrelation = checkRegionCorrelation(transactions);

        //get the worst status
        Status errorLevel = Stream.of(amountStatus, ip, number, ipCorrelation,
                regionCorrelation).max(Comparator.comparing(Enum::ordinal)).get();

        //create info
        String info = (errorLevel == amountStatus ? "amount, ": "") +
                (errorLevel == number ? "card-number, ": "") +
                (errorLevel == ip? "ip, ": "") +
                (errorLevel == ipCorrelation ? "ip-correlation, ": "") +
                (errorLevel == regionCorrelation ? "region-correlation, ": "");

        info = errorLevel == Status.ALLOWED ? "none" : info.substring(0, info.length() - 2);

        transaction.setResult(errorLevel);
        transactionRepo.save(transaction);
        return Map.of("result", errorLevel.toString(), "info", info);
    }

    private void updateLimits(String cardNumber) {
        Optional<CardNumber_limit> limit = cardNumber_limitRepo.findByNumber(cardNumber);
        if (limit.isEmpty()) {
            MAX_ALLOWED = 200L;
            MAX_MANUAL = 1500L;
            CardNumber_limit currCard = new CardNumber_limit(cardNumber, MAX_ALLOWED, MAX_MANUAL);
            cardNumber_limitRepo.save(currCard);
        } else {
            MAX_ALLOWED = limit.get().getMAX_ALLOWED();
            MAX_MANUAL = limit.get().getMAX_MANUAL();
        }
    }

    private Status checkAmount(Long amount) {
        if (amount <= MAX_ALLOWED) {
            return (Status.ALLOWED);
        } else if (amount > MAX_MANUAL) {
            return (Status.PROHIBITED);
        } else {
            return (Status.MANUAL_PROCESSING);
        }
    }

    private Status checkIp(String ip) {
        try {
            ipService.findIpByAddress(ip);
            return Status.PROHIBITED;
        } catch (IpNotFoundException e) {
            return Status.ALLOWED;
        }
    }

    private Status checkNumber(String number) {
        try {
            cardService.findCardByNumber(number);
            return Status.PROHIBITED;
        } catch (CardNumberNotFound e) {
            return Status.ALLOWED;
        }
    }

    private Status checkIpCorrelation(List<TransactionInfo> transactions) {
        long count = transactions.stream().map(TransactionInfo::getIp).distinct().count();
        return count < MAX_DIFFERENCE_SOURCE ? Status.ALLOWED :
                count == MAX_DIFFERENCE_SOURCE ? Status.MANUAL_PROCESSING:
                        Status.PROHIBITED;
    }

    private Status checkRegionCorrelation(List<TransactionInfo> transactions) {
        long count = transactions.stream().map(TransactionInfo::getRegion).distinct().count();
        return count <  MAX_DIFFERENCE_SOURCE ? Status.ALLOWED :
                count ==  MAX_DIFFERENCE_SOURCE ? Status.MANUAL_PROCESSING:
                        Status.PROHIBITED;
    }

    public List<TransactionInfo> getAllTransaction() {
        return transactionRepo.findAll();
    }

    public List<TransactionInfo> getTransactionsByNumber(String number) {
        boolean valid = StolenCardService.ValidateCard(number);
        if (!valid) {
            throw new BadRequestException(number + " is not a valid card number");
        }
        List<TransactionInfo> list = transactionRepo.findByNumberOrderByTransactionId(number);
        if (list.isEmpty()) {
            throw new CardNumberNotFound(number + "'s history is not found in database");
        }
        return list;
    }

    @Transactional
    public TransactionInfo putFeedback(FeedBack feedBack) {
        Optional<TransactionInfo> transaction = transactionRepo.findById(feedBack.getTransactionId());
        //handle exceptions
        if (transaction.isEmpty()) {
            throw new TransactionNotFoundException("Transaction ID " + feedBack.getTransactionId() + " is not found");
        }
        TransactionInfo transactionInfo = transaction.get();
        if (transactionInfo.getFeedback() != null) {
            throw new FeedbackExistException();
        }

        //update current card's limit
        String cardNumber = transactionInfo.getNumber();
        Optional<CardNumber_limit> limit = cardNumber_limitRepo.findByNumber(cardNumber);
        MAX_ALLOWED = limit.orElseThrow().getMAX_ALLOWED();
        MAX_MANUAL = limit.orElseThrow().getMAX_MANUAL();


        //modify limits according feedback and save new limit + card number in database
        long amount = transactionInfo.getAmount();
        Status result = transactionInfo.getResult();
        Status feedbackStatus = switch (feedBack.getFeedback()) {
            case "ALLOWED" -> Status.ALLOWED;
            case "MANUAL_PROCESSING" -> Status.MANUAL_PROCESSING;
            case "PROHIBITED" -> Status.PROHIBITED;
            default ->
                    throw new BadRequestException("Feedback can not be " + feedBack.getFeedback() + " but ALLOWED or MANUAL_PROCESSING or PROHIBITED");
        };

        if (result != feedbackStatus) {
            if (result == Status.ALLOWED && feedbackStatus == Status.MANUAL_PROCESSING) {
                MAX_ALLOWED = decreaseLimit(MAX_ALLOWED, amount);
            }
            if (result == Status.ALLOWED && feedbackStatus == Status.PROHIBITED) {
                MAX_ALLOWED = decreaseLimit(MAX_ALLOWED, amount);
                MAX_MANUAL = decreaseLimit(MAX_MANUAL, amount);
            }
            if (result == Status.MANUAL_PROCESSING && feedbackStatus == Status.ALLOWED) {
                MAX_ALLOWED = increaseLimit(MAX_ALLOWED, amount);
            }
            if (result == Status.MANUAL_PROCESSING && feedbackStatus == Status.PROHIBITED) {
                MAX_MANUAL = decreaseLimit(MAX_MANUAL, amount);
            }
            if (result == Status.PROHIBITED && feedbackStatus == Status.ALLOWED) {
                MAX_ALLOWED = increaseLimit(MAX_ALLOWED, amount);
                MAX_MANUAL = increaseLimit(MAX_MANUAL, amount);
            }
            if (result == Status.PROHIBITED && feedbackStatus == Status.MANUAL_PROCESSING) {
                MAX_MANUAL = increaseLimit(MAX_MANUAL, amount);
            }
        } else {
            throw new BadFeedbackException("Your feedback " + feedbackStatus + " is equal to the result" );
        }
        //save transaction
        transactionInfo.setFeedback(feedbackStatus);
        transactionRepo.save(transactionInfo);
        //save new limit
        CardNumber_limit curr = cardNumber_limitRepo.findByNumber(cardNumber).orElseThrow();
        curr.setMAX_ALLOWED(MAX_ALLOWED);
        curr.setMAX_MANUAL(MAX_MANUAL);
        cardNumber_limitRepo.save(curr);

        return transactionInfo;
    }
    private long increaseLimit(long limit, long amount) {
        return (long) Math.ceil(0.8 * limit + 0.2 * amount);
    }
    private long decreaseLimit(long limit, long amount) {
        return (long) Math.ceil(0.8 * limit - 0.2 * amount);
    }

}
