package com.osy.commerce.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String email;
    private String name;
    private Set<String> roles;
}
