package com.mars.NangPaGo.security.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;


@Getter
@Entity
public class Token {
    @Id
    private String tokenId;
    private String accessToken;
    private String refreshToken;

    @Builder
    public Token(String tokenId, String accessToken, String refreshToken) {
        this.tokenId = tokenId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    //리프래쉬 토큰 재발급
    public Token reissueRefreshToken(String refreshtoken){
        this.refreshToken = refreshtoken;
        return this;
    }
    //엑세스 토큰 재발급
    public void reissueAccessToken(String accesstoken){
        this.accessToken = accesstoken;
    }
}
