package com.mars.NangPaGo.auth.handler;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;

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

        User user = validateUser(email);

        if (isDuplicatedEmail(user, provider)) {
            redirectToErrorPage(response, user.getOauth2Provider().name());
            return;
        }

        renewOauth2ProviderToken(authentication, email);
        issueJwtTokens(response, user, email, authentication);
        response.sendRedirect(clientHost);
    }

    private User validateUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자 검증 에러: " + email));
    }

    private boolean isDuplicatedEmail(User user, String provider) {
        return user.getOauth2Provider().name().equals(provider);
    }

    private void redirectToErrorPage(HttpServletResponse response, String existingProvider) throws IOException {
        String encodedMessage = URLEncoder.encode(existingProvider, StandardCharsets.UTF_8);
        response.sendRedirect(clientHost + "/oauth/error?existingProvider=" + encodedMessage);
    }

    private void renewOauth2ProviderToken(Authentication authentication, String email) {
        OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(authentication);
        if (validateAuthorizedClient(authorizedClient)) {
            String refreshToken = authorizedClient.getRefreshToken().getTokenValue();
            String clientName = authorizedClient.getClientRegistration().getClientName();

            oauth2ProviderTokenService.renewOauth2ProviderToken(clientName, refreshToken, email);
        }
    }

    private boolean validateAuthorizedClient(OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient != null && authorizedClient.getRefreshToken() != null;
    }

    private void issueJwtTokens(HttpServletResponse response, User user, String email, Authentication authentication) {
        Long userId = user.getId();
        String role = getRole(authentication);

        String access = jwtUtil.createJwt("access", userId, email, role, jwtUtil.getAccessTokenExpireMillis());
        String refresh = jwtUtil.createJwt("refresh", userId, email, role, jwtUtil.getRefreshTokenExpireMillis());

        tokenService.renewRefreshToken(email, refresh);

        response.addCookie(createCookie("access", access, jwtUtil.getAccessTokenExpireMillis(), false));
        response.addCookie(createCookie("refresh", refresh, jwtUtil.getRefreshTokenExpireMillis(), false));
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

        return OAuth2AuthorizedClientManager.authorize(
            OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal(authentication)
                .build()
        );
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
