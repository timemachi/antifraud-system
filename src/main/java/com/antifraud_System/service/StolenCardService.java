package com.antifraud_System.service;


import com.antifraud_System.entity.StolenCard;
import com.antifraud_System.exception.BadRequestException;
import com.antifraud_System.exception.CardExistException;
import com.antifraud_System.exception.CardNumberNotFound;
import com.antifraud_System.reposiroty.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StolenCardService {
    @Autowired
    StolenCardRepository cardRepository;

    public StolenCard findCardByNumber(String number) {
        Optional<StolenCard> card = cardRepository.findByNumber(number);
        if (card.isEmpty()) {
            throw new CardNumberNotFound(number + " has not found");
        }
        return card.get();
    }

    @Transactional
    public StolenCard addCard(StolenCard card) {
        try{
            findCardByNumber(card.getNumber());
            throw new CardExistException();
        } catch (CardNumberNotFound ignored) {}

        cardRepository.save(card);
        return cardRepository.findByNumber(card.getNumber()).orElseThrow();
    }
    public List<StolenCard> getAllCard() {
        return cardRepository.findAll();
    }

    @Transactional
    public Map<String,String> deleteCardNumber(String number) {
        if (!ValidateCard(number)) {
            throw new BadRequestException("card number's format is not valid");
        }
        try {
            StolenCard card = findCardByNumber(number);
            cardRepository.delete(card);
            return Map.of("status", "Card " + number + " successfully removed!");
        } catch (CardNumberNotFound e) {
            throw new CardNumberNotFound(number + " is not found so we can not delete it");
        }

    }

    static public boolean ValidateCard(String number) {
        String regexp = "\\d{16}";
        if (!number.matches(regexp)) {
            return false;
        }
        int[] array = convertToIntArray(number);
        int checksum = array[array.length - 1];
        array[array.length - 1] = 0;
        multiplyOddNumbersByTwo(array);
        subtractNineFromNumbersOverNine(array);
        int sum = addAllNumbers(array);
        return (sum + checksum) % 10 == 0;
    }
    private static int addAllNumbers(int[] array) {
        int sum = 0;
        for (int i: array) {
            sum += i;
        }
        return sum;
    }

    private static void subtractNineFromNumbersOverNine(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 9) {
                array[i] -= 9;
            }
        }
    }

    private static void multiplyOddNumbersByTwo(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if ((i+1) % 2 != 0) {
                array[i] *= 2;
            }
        }
    }

    private static int[] convertToIntArray(String number) {
        int[] convertedIntArray = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            convertedIntArray[i] = number.charAt(i) - 48;
        }
        return convertedIntArray;
    }



}
