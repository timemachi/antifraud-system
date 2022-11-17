package com.antifraud_System.controller;



import com.antifraud_System.entity.FeedBack;
import com.antifraud_System.entity.TransactionInfo;
import com.antifraud_System.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Controller
public class AntifraudController {
    @Autowired
    TransactionService transactionService;

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping("/api/antifraud/transaction/**")
    public ResponseEntity<Map<String,String>> processTransaction (@RequestBody @Valid TransactionInfo transactionInfo) {

        return ResponseEntity.ok(transactionService.addTransaction(transactionInfo));
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/api/antifraud/history/**")
    public ResponseEntity<List<TransactionInfo>> getAllTransaction() {
        return ResponseEntity.ok(transactionService.getAllTransaction());
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity<List<TransactionInfo>> getTransactionsByNumber(@PathVariable String number) {
        return ResponseEntity.ok(transactionService.getTransactionsByNumber(number));
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<TransactionInfo> putFeedback(@RequestBody @Valid FeedBack feedBack) {
        return ResponseEntity.ok(transactionService.putFeedback(feedBack));
    }




}
