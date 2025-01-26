package com.mars.app.auth.handler;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.app.auth.vo.OAuth2UserImpl;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import com.mars.app.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.app.domain.auth.service.TokenService;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final CookieUtil cookieUtil;
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

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자 검증 에러: " + email));

        if (isEmailAlreadyRegisteredForSignUp(user, provider)) {
            redirectToErrorPage(response, user.getOauth2Provider().name());
            return;
        }

        renewOauth2ProviderToken(authentication, email);
        issueAccessAndRefreshTokens(response, user, email, authentication);
        response.sendRedirect(clientHost);
    }

    private boolean isEmailAlreadyRegisteredForSignUp(User user, String provider) {
        return !user.getOauth2Provider().name().equals(provider);
    }

    private void redirectToErrorPage(HttpServletResponse response, String existingProvider) throws IOException {
        String encodedTitle = URLEncoder.encode("이미 가입된 계정입니다.", StandardCharsets.UTF_8);
        String encodedDescription = URLEncoder.encode(
            "해당 이메일은 " + existingProvider + " 계정으로 이미 가입되어 있습니다.\n기존 계정으로 로그인하거나 다른 방법을 사용해 주세요.",
            StandardCharsets.UTF_8);
        response.sendRedirect(clientHost + "/error?title=" + encodedTitle + "&description=" + encodedDescription);
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

    private void issueAccessAndRefreshTokens(HttpServletResponse response, User user, String email,
        Authentication authentication) {
        Long userId = user.getId();
        String role = getRole(authentication);

        String access = jwtUtil.createJwt(CookieUtil.ACCESS_TOKEN_NAME, userId, email, role,
            jwtUtil.getAccessTokenExpireMillis());
        String refresh = jwtUtil.createJwt(CookieUtil.REFRESH_TOKEN_NAME, userId, email, role,
            jwtUtil.getRefreshTokenExpireMillis());

        tokenService.renewRefreshToken(email, refresh);

        cookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN_NAME, access, jwtUtil.getAccessTokenExpireMillis(),
            false);
        cookieUtil.addCookie(response, CookieUtil.REFRESH_TOKEN_NAME, refresh, jwtUtil.getRefreshTokenExpireMillis(),
            false);
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
}
