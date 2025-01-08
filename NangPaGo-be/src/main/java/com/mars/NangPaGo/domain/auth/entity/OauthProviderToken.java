package com.mars.NangPaGo.domain.auth.entity;

import com.mars.NangPaGo.auth.enums.OAuth2Provider;
import com.mars.NangPaGo.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class OauthProviderToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private OAuth2Provider oauth2Provider;

    @Column(name = "refresh_token")
    private String providerRefreshToken;

    private String email;

    @Builder
    private OauthProviderToken(OAuth2Provider oauth2Provider, String providerRefreshToken, String email){
        this.oauth2Provider = oauth2Provider;
        this.providerRefreshToken = providerRefreshToken;
        this.email = email;
    }

    private static OauthProviderToken of(OAuth2Provider oauth2Provider, String providerRefreshToken, String email){
        return OauthProviderToken.builder()
            .oauth2Provider(oauth2Provider)
            .providerRefreshToken(providerRefreshToken)
            .email(email)
            .build();
    }
}
