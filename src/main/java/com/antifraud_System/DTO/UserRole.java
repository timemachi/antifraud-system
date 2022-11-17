package com.antifraud_System.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRole {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "SUPPORT|MERCHANT")
    private String role;
}
