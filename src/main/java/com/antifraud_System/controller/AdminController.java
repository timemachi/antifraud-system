package com.antifraud_System.controller;


import com.antifraud_System.DTO.UserEntityDTO;
import com.antifraud_System.DTO.UserLock;
import com.antifraud_System.DTO.UserRole;
import com.antifraud_System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/api/auth/role/**")
    public ResponseEntity<UserEntityDTO> assignRole(@RequestBody @Valid UserRole userRole) {
        return ResponseEntity.ok(userService.assignRoleToUser(userRole));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @GetMapping("/api/auth/{id}")
    public ResponseEntity<UserEntityDTO> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(new UserEntityDTO(userService.findUserById(id)));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @GetMapping("/api/auth/username/{username}")
    public ResponseEntity<UserEntityDTO> getUserByUserName(@PathVariable String username) {
        return ResponseEntity.ok(new UserEntityDTO(userService.findUserByUsername(username)));
    }


    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @GetMapping("/api/auth/list")
    public ResponseEntity<List<UserEntityDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/api/auth/access/**")
    public ResponseEntity<Map<String, String>> operationLock(@RequestBody @Valid UserLock userLock) {
        userService.lockOperation(userLock);
        String name = userLock.getUsername();
        String operation = userLock.getOperation().equals("LOCK") ? "locked": "unlocked";
        return ResponseEntity.ok(Map.of("status", "User " + name + " " + operation + "!"));
    }


    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.deleteUser(username));
    }


}
