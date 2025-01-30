package com.mars.app.provider;

import com.mars.common.model.user.User;
import com.mars.common.util.web.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class TestJwtProvider {
    private final JwtUtil jwtUtil;

    public TestJwtProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createTestAccessToken(User user) {
        return jwtUtil.createJwt("access", user.getId(), user.getEmail(), user.getRole(), 3600000L); // 1시간
    }

    public String createTestRefreshToken(String email, String role) {
        return jwtUtil.createJwt("refresh", 1L, email, role, 86400000L); // 24시간
    }
}
