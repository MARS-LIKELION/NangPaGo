package com.mars.app.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class OAuthProviderToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider")
    private String providerName;

    @Column(name = "refresh_token")
    private String providerRefreshToken;

    private String email;

    @Builder
    private OAuthProviderToken(String providerName, String providerRefreshToken, String email) {
        this.providerName = providerName;
        this.providerRefreshToken = providerRefreshToken;
        this.email = email;
    }

    public void updateRefreshToken(String providerRefreshToken){
        this.providerRefreshToken = providerRefreshToken;
    }

    public static OAuthProviderToken of(String providerName, String providerRefreshToken, String email) {
        return OAuthProviderToken.builder()
            .providerName(providerName)
            .providerRefreshToken(providerRefreshToken)
            .email(email).build();
    }
}
