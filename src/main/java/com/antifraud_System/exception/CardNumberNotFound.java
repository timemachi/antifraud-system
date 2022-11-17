package com.antifraud_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CardNumberNotFound extends RuntimeException{

    public CardNumberNotFound(String msg) {
        super(msg);
    }
}
