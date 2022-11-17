package com.antifraud_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "A similar username already exists!")
public class UserExistException extends RuntimeException{
}
