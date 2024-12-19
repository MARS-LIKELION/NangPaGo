package com.mars.NangPaGo.security.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Token {
    @Id
    private String tokenid;
    private String accesstoken;
    private String refreshtoken;

    //리프래쉬 토큰 재발급
    public Token updateRefreshToken(String refreshtoken){
        this.refreshtoken = refreshtoken;
        return this;
    }
    //엑세스 토큰 재발급
    public void updateAccessToken(String accesstoken){
        this.accesstoken = accesstoken;
    }
}
