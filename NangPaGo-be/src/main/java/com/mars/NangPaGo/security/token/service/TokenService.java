package com.mars.NangPaGo.security.token.service;

import com.mars.NangPaGo.security.token.entity.Token;
import com.mars.NangPaGo.security.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private TokenRepository tokenRepository;

    //리프래쉬 토큰의 갱신, DB에 저장하는 토큰의 경우 리프래쉬밖에 없어서 리프래쉬만 사용
    @Transactional
    public void saveOrUpdate (Token token, String memberId, String accessToken, String refreshToken){
        if(token!=null){//이미 토큰이 있을 경우, 만료된 토큰을 변경함
            token.updateAccessToken(accessToken);
            token.updateRefreshToken(refreshToken);
        }else{//토큰이 아예 없는 새로 가입한 회원의 경우
            token = Token.builder()
                    .tokenid(memberId) //사용자 고유의 Id
                    .accesstoken(accessToken)//엑세스 토큰
                    .refreshtoken(refreshToken)//리프래쉬 토큰
                    .build();
        }
        tokenRepository.save(token);
    }
    @Transactional(readOnly = true)
    public Token findAccessToken(String accessToken) {//예외처리 만들기, 임시로 런타임
        return tokenRepository.findByAccesstoken(accessToken).orElseThrow(() -> new RuntimeException("만료된 토큰입니다"));
    }
    @Transactional
    public void updateToken(String accessToken, Token token){
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
