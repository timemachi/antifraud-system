package com.antifraud_System.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserLock {

    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Operation cannot be blank")
    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}
