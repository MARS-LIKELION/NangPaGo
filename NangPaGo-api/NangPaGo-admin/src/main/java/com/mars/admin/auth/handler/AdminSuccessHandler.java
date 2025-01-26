package com.mars.admin.auth.handler;

import static com.mars.common.exception.NPGExceptionType.FORBIDDEN;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.admin.auth.vo.UserDetailsImpl;
import com.mars.admin.domain.auth.service.TokenService;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.model.user.User;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${client.host}")
    private String clientHost;

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final TokenService tokenService;

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자 검증 에러: " + email));

        if (!user.getRole().equals("ROLE_ADMIN")) {
            throw FORBIDDEN.of("해당 아이디로 접근할 수 없습니다.");
        }

        issueAccessAndRefreshTokens(response, user, email, authentication);
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
}
