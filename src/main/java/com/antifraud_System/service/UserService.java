package com.antifraud_System.service;


import com.antifraud_System.DTO.UserEntityDTO;
import com.antifraud_System.DTO.UserLock;
import com.antifraud_System.DTO.UserRole;
import com.antifraud_System.entity.Role;
import com.antifraud_System.entity.UserEntity;
import com.antifraud_System.exception.BadRequestException;
import com.antifraud_System.exception.RoleAssignedException;
import com.antifraud_System.exception.UserExistException;
import com.antifraud_System.exception.UserNotFoundException;
import com.antifraud_System.reposiroty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"UserEntity"})
public class UserService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Cacheable(cacheNames = "name", key = "#name", sync = true)
    public UserEntity findUserByUsername(String name) {
        Optional<UserEntity> user = userRepo.findByUsernameIgnoreCase(name);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Not Found " + name);
        }
        try {
            Thread.sleep(3000);
            System.out.println("Find in H2 database");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user.get();
    }

    @Cacheable(cacheNames = "id", key = "#id",sync = true)
    public UserEntity findUserById(long id) {
        Optional<UserEntity> user = userRepo.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Not Found " + id);
        }
        try {
            Thread.sleep(3000);
            System.out.println("Find in H2 database");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user.get();
    }

    public UserEntityDTO addUser(UserEntity user) {
        try {
            findUserByUsername(user.getUsername());
        } catch (UserNotFoundException e) {
            String password = passwordEncoder.encode(user.getPassword());
            user.setPassword(password);
            if (userRepo.count() == 0) {
                user.setRole(Role.ADMINISTRATOR);
                user.setNotLocked(true);
            } else {
                user.setRole(Role.MERCHANT);
                user.setNotLocked(false);
            }
            userRepo.save(user);
            return new UserEntityDTO(findUserByUsername(user.getUsername()));
        }
        throw new UserExistException();
    }

    public Map<String, String> deleteUser(String username) {
        Optional<UserEntity> user = userRepo.findByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Can not Delete because not found" + username);
        }
        if (user.get().getRole().equals(Role.ADMINISTRATOR)) {
            throw new BadRequestException("Can not delete Administrator");
        }
        userRepo.delete(user.get());
        return Map.of("username", username, "status", "Deleted successfully!");
    }

    public UserEntityDTO assignRoleToUser(UserRole userRole) {
        UserEntity user = findUserByUsername(userRole.getUsername());
        String roleName = userRole.getRole();
        Role role;
        if (roleName.equals("SUPPORT")) {
            role = Role.SUPPORT;
        } else {
            role = Role.MERCHANT;
        }
        if (user.getRole().equals(role)) {
            throw new RoleAssignedException();
        }
        user.setRole(role);
        userRepo.save(user);
        return new UserEntityDTO(user);
    }

    public void lockOperation(UserLock userLock) {

        try {
            UserEntity user = findUserByUsername(userLock.getUsername());
            if (user.getRole().equals(Role.ADMINISTRATOR)) {
                throw new BadRequestException("Can not lock Administrator");
            }
            boolean notLock = !userLock.getOperation().equals("LOCK");
            user.setNotLocked(notLock);
            userRepo.save(user);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("stuck in lockOperation: " + userLock.getUsername());
        }

    }

    public List<UserEntityDTO> getAllUser() {
        return userRepo.findAll().stream().map(UserEntityDTO::new).collect(Collectors.toList());
    }

}
