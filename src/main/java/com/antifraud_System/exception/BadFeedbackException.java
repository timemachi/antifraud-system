package com.antifraud_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class BadFeedbackException extends RuntimeException{
    public BadFeedbackException(String msg) {
        super(msg);
    }
}
