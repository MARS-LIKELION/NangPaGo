package com.mars.common.model.auth;

import com.mars.common.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private OAuthProviderToken(String providerName, String providerRefreshToken, User user) {
        this.providerName = providerName;
        this.providerRefreshToken = providerRefreshToken;
        this.user = user;
    }

    public void updateRefreshToken(String providerRefreshToken){
        this.providerRefreshToken = providerRefreshToken;
    }

    public static OAuthProviderToken of(String providerName, String providerRefreshToken, User user) {
        return OAuthProviderToken.builder()
            .providerName(providerName)
            .providerRefreshToken(providerRefreshToken)
            .user(user)
            .build();
    }
}
