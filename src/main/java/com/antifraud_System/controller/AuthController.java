package com.antifraud_System.controller;



import com.antifraud_System.DTO.UserEntityDTO;
import com.antifraud_System.entity.UserEntity;
import com.antifraud_System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@Controller
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserEntityDTO> addUser(@RequestBody @Valid UserEntity user) {
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }
}
