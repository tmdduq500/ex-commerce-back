package com.osy.commerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public final class UserProfileDto {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String status;
    private LocalDateTime lastLoginAt;
}
