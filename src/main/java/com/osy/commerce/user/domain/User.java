package com.osy.commerce.user.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false, length = 32)
    private String role;

    @Column(length = 32)
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
