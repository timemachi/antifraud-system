package com.antifraud_System.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class FeedBack {
    @Positive
    private long transactionId;
    @Pattern(regexp = "ALLOWED|MANUAL_PROCESSING|PROHIBITED")
    private String feedback;
}
