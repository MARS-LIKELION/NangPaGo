package com.mars.NangPaGo.domain.jwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private LocalDateTime expiration;
    private String refreshToken;

    private RefreshToken(String email, String refreshToken, LocalDateTime expiration) {
        this.email = email;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public static RefreshToken create(String email, String refreshToken, LocalDateTime expiration) {
        return new RefreshToken(email, refreshToken, expiration);
    }
}
