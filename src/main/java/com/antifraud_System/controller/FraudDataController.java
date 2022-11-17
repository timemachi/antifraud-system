package com.antifraud_System.controller;


import com.antifraud_System.entity.StolenCard;
import com.antifraud_System.entity.SuspiciousIP;
import com.antifraud_System.service.StolenCardService;
import com.antifraud_System.service.SuspiciousIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class FraudDataController {
    @Autowired
    SuspiciousIpService ipService;

    @Autowired
    StolenCardService cardService;

    @PreAuthorize("hasRole('SUPPORT')")
    @PostMapping("/api/antifraud/suspicious-ip/**")
    public ResponseEntity<SuspiciousIP> addSuspiciousIP(@RequestBody @Valid SuspiciousIP ip) {
        return ResponseEntity.ok(ipService.addIP(ip));
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<Map<String, String>> deleteIp(@PathVariable String ip) {
        return ResponseEntity.ok(ipService.deleteIp(ip));
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/api/antifraud/suspicious-ip/**")
    public ResponseEntity<List<SuspiciousIP>> getAllIp() {
        return ResponseEntity.ok(ipService.getAllIp());
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @PostMapping("/api/antifraud/stolencard/**")
    public ResponseEntity<StolenCard> addStolenCard(@RequestBody @Valid StolenCard card) {
        return ResponseEntity.ok(cardService.addCard(card));
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable String number) {
        return ResponseEntity.ok(cardService.deleteCardNumber(number));
    }


    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<List<StolenCard>> getAllCard() {
        return ResponseEntity.ok(cardService.getAllCard());
    }


}
