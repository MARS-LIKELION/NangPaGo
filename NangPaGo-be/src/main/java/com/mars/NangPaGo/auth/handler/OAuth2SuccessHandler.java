package com.mars.NangPaGo.auth.handler;

import com.mars.NangPaGo.auth.vo.OAuth2UserImpl;
import com.mars.NangPaGo.common.util.JwtUtil;
import com.mars.NangPaGo.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.NangPaGo.domain.auth.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${client.host}")
    private String clientHost;

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final OAuth2ProviderTokenService oauth2ProviderTokenService;
    private final OAuth2AuthorizedClientManager OAuth2AuthorizedClientManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication)
        throws IOException {
        String email = ((OAuth2UserImpl) authentication.getPrincipal()).getName();

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauth2Token.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient authorizedClient = OAuth2AuthorizedClientManager.authorize(
            OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal(authentication)
                .build()
        );

        if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
            // 구글은 최초 로그인에만 가져와짐
            String refreshToken = authorizedClient.getRefreshToken().getTokenValue();
            String clientName = authorizedClient.getClientRegistration().getClientName();

            oauth2ProviderTokenService.checkOauth2ProviderToken(clientName, refreshToken, email);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("사용자 권한이 설정되지 않았습니다."));

        String access = jwtUtil.createJwt("access", email, role, jwtUtil.getAccessTokenExpireMillis());
        String refresh = jwtUtil.createJwt("refresh", email, role, jwtUtil.getRefreshTokenExpireMillis());

        tokenService.renewRefreshToken(email, refresh);

        response.addCookie(createCookie("access", access, jwtUtil.getAccessTokenExpireMillis(), false));
        response.addCookie(createCookie("refresh", refresh, jwtUtil.getRefreshTokenExpireMillis(), false));
        response.sendRedirect(clientHost);
    }

    private Cookie createCookie(String key, String value, long expireMillis, boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (expireMillis / 1000));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }
}
