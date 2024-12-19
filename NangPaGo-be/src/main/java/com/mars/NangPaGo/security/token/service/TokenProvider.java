package com.mars.NangPaGo.security.token.service;

import com.mars.NangPaGo.security.token.entity.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    @Value("${jwt.key}")
    private String key;
    private SecretKey secretKey; //생성할 키를 담을 객체

    private static final long ACCESS_TOKEN_VALID_DATE = 1000 * 60 * 30; //엑세스 토큰 유효기간 30분
    private static final long REFRESH_TOKEN_VALID_DATE = 1000 * 60 * 60 * 24* 7L; //리프래쉬 토큰 유효기간 7일
    private static final String ROLE ="role";

    private final TokenService tokenService;

    @PostConstruct() //의존성 주입 이후에 실행, 어플리케이션 실행 시 1회 자동으로 실행
    private void setSecretKey(String key){
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }
    //엑세스 토큰을 저장할 쿠키 생성
    public Cookie generateCookie(String accessToken){
        String cookieName = "accessToken";
        String cookieValue = accessToken;
        Cookie cookie = new Cookie(cookieName,cookieValue);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60*30); //30분

        return cookie;
    }

    private String generateToken(Authentication authentication, long validDate){
        Date data = new Date();
        Date validDateNow = new Date(data.getTime()+validDate);

        String authories = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .subject(authentication.getName()) //시큐리티 컨텍스트 홀더에서 가져올 유저의 Id값
                .claim(ROLE,authories)//여러개의 권한은 없지만 기본적으로 시큐리티 익명과 로그인 유저를 구분짓기 위해 추가
                .issuedAt(data)//발급 기간
                .expiration(validDateNow)//유효기간
                .signWith(secretKey, Jwts.SIG.HS256)//JWT 시크릿키, 기존 암호화된 시크릿 키를 한번 더 암호화
                .compact();
    };
    private String generateAccessToken(Authentication authentication){
        //엑세스 토큰 생성, 새로 발급받을때 다른 메서드 호출로 RefreshToken에 저장
        return generateToken(authentication,ACCESS_TOKEN_VALID_DATE);
    }

    private void generateRefreshToken(Token token, Authentication authentication, String accessToken){
        String refreshToken = generateToken(authentication,REFRESH_TOKEN_VALID_DATE);
        //토큰의 null 여부에 따라 update 또는 save를 함
        tokenService.saveOrUpdate(token, authentication.getName(),accessToken,refreshToken);
    }
    //엑세스 토큰 재발급
    private String reissueAccessToken(String accessToken){
        if(StringUtils.hasText(accessToken)){//토큰 값이 전달되었을때
            Token token = tokenService.findAccessToken(accessToken);//해당 토큰값이 있는 리프래쉬 토큰 확인
            String refreshtoken = token.getRefreshtoken(); //리프래시 토큰의 값 저장

            if(validateToken(refreshtoken)){//리프래쉬 토큰의 유효기간이 남았을 경우
                //엑세스 토큰을 새로 발급하여, 리프래쉬 토큰의 정보를 새로 갱신
                String newAccessToken = generateAccessToken(getAuthentication(refreshtoken));
                tokenService.updateToken(newAccessToken,token);
                return newAccessToken;
            }//리프래시 토큰이 유효하지 않는 경우는 filter를 통해 거를예정이기에 추가적인 if문 사용X
        }return null;
    }
    //토큰 유효성 검사
    private boolean validateToken(String token){
        if (!StringUtils.hasText(token)){
            return false;
        }
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date()); //유효기간이 남으면 true
    }

    //토큰의 저장되어있는 Payload부분 가져오기
    private Claims parseClaims(String token){
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        }catch (ExpiredJwtException e){//토큰 만료되었을 경우
            return e.getClaims();
        }catch (MalformedJwtException e){//올바르지 않은 토큰
            throw new MalformedJwtException("올바르지 않은 토큰입니다."); //예외처리 따로 하기
        }catch (SecurityException e){// 잘못된 JWT 시그니처
            throw new SecurityException("잘못된 시그니처 입니다."); //예외처리
        }
    }
    //권한 가져오기
    public List<SimpleGrantedAuthority> getAuthorities(Claims claims){
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(ROLE).toString()));
    }

    //유저 객체 생성 이후 시큐리티 ContextHolder의 Context에 등록
    public Authentication getAuthentication(String token){
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorityList = getAuthorities(claims);

        //시큐리티의 유저 클래스로 저장 (이름, 비밀번호는 oauth사용으로 없음, 권한)
        User user = new User(claims.getSubject(),"",authorityList);
        //유저 객체 생성 이후 인증 완료한 Authentication객체 생성
        return new UsernamePasswordAuthenticationToken(user, token, authorityList);
    }

}
