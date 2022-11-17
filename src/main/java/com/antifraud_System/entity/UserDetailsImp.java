package com.antifraud_System.entity;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImp implements UserDetails {

    private final String username;
    private final String password;

    private final List<GrantedAuthority> grantedAuthorities;

    private final boolean isNotLocked;

    public UserDetailsImp(UserEntity user) {
        username = user.getUsername();
        password = user.getPassword();
        grantedAuthorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
        isNotLocked = user.isNotLocked();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNotLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
