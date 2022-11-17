package com.antifraud_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Feedback already exists!")
public class FeedbackExistException extends RuntimeException{
}
