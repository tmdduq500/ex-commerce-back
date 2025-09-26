package com.osy.commerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public final class UserProfileDto {
    private Long id;
    private String email;
    private String name;
    private Set<String> roles;
    private String status;
    private LocalDateTime lastLoginAt;
}
