package com.mars.NangPaGo.domain.user.entity;

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

    private String refreshToken;
    private String email;
    private LocalDateTime expiration;

    private RefreshToken(String refreshToken, String email, LocalDateTime expiration) {
        this.refreshToken = refreshToken;
        this.email = email;
        this.expiration = expiration;
    }

    public static RefreshToken create(String refreshToken, String email, LocalDateTime expiration) {
        return new RefreshToken(refreshToken, email, expiration);
    }
}
