package com.mars.NangPaGo.auth.handler;

import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.auth.service.TokenService;
import com.mars.NangPaGo.common.util.JwtUtil;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.auth.vo.OAuth2UserImpl;
import com.mars.NangPaGo.domain.auth.service.OAuth2ProviderTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        OAuth2UserImpl oauth2User = (OAuth2UserImpl) authentication.getPrincipal();
        String provider = (String) oauth2User.getAttributes().get("provider");
        String email = oauth2User.getName();

        if (isDuplicatedEmail(response, email, provider)) {
            return;
        }

        renewOauth2ProviderToken(authentication, email);

        String role = getRole(authentication);
        String access = jwtUtil.createJwt("access", email, role, jwtUtil.getAccessTokenExpireMillis());
        String refresh = jwtUtil.createJwt("refresh", email, role, jwtUtil.getRefreshTokenExpireMillis());

        tokenService.renewRefreshToken(email, refresh);

        response.addCookie(createCookie("access", access, jwtUtil.getAccessTokenExpireMillis(), false));
        response.addCookie(createCookie("refresh", refresh, jwtUtil.getRefreshTokenExpireMillis(), false));
        response.sendRedirect(clientHost);
    }

    private boolean isDuplicatedEmail(HttpServletResponse response, String email, String provider)
        throws IOException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> NPGExceptionType.NOT_FOUND_USER.of("사용자 검증 에러: " + email));

        if (!user.getOauth2Provider().name().equals(provider)) {
            String existingProvider = user.getOauth2Provider().name();
            String encodedMessage = URLEncoder.encode(existingProvider, StandardCharsets.UTF_8);
            response.sendRedirect(clientHost + "/oauth/error?existingProvider=" + encodedMessage);
            return true;
        }

        return false;
    }

    private void renewOauth2ProviderToken(Authentication authentication, String email) {
        OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authentication);
        if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
            // 구글은 최초 로그인에만 가져와짐
            String refreshToken = authorizedClient.getRefreshToken().getTokenValue();
            String clientName = authorizedClient.getClientRegistration().getClientName();

            oauth2ProviderTokenService.renewOauth2ProviderToken(clientName, refreshToken, email);
        }
    }

    private String getRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("사용자 권한이 설정되지 않았습니다."));
    }

    private OAuth2AuthorizedClient getOAuth2AuthorizedClient(Authentication authentication) {
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauth2Token.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient authorizedClient = OAuth2AuthorizedClientManager.authorize(
            OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal(authentication)
                .build()
        );
        return authorizedClient;
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
