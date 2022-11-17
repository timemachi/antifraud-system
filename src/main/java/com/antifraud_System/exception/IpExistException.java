package com.antifraud_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "A similar ip already exists!")
public class IpExistException extends RuntimeException{
}
