package com.antifraud_System.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class CardNumber_limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String number;

    private Long MAX_ALLOWED;

    private Long MAX_MANUAL;

    public CardNumber_limit(String number, Long MAX_ALLOWED, Long MAX_MANUAL) {
        this.number = number;
        this.MAX_ALLOWED = MAX_ALLOWED;
        this.MAX_MANUAL = MAX_MANUAL;
    }
}
