package com.mars.admin.auth.vo;

import com.mars.common.dto.user.UserResponseDto;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final UserResponseDto userResponseDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userResponseDto.role()));
    }

    @Override
    public String getPassword() {
        return userResponseDto.password();
    }

    @Override
    public String getUsername() {
        return userResponseDto.email();
    }
}
