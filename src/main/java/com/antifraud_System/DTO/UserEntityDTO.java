package com.antifraud_System.DTO;


import com.antifraud_System.entity.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEntityDTO {
    private Long id;
    private String name;
    private String username;
    private String role;

    public UserEntityDTO(UserEntity user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole().getName().substring(5);
    }
}
