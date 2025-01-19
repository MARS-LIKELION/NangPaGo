package com.mars.app.provider;

import com.mars.app.common.util.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class TestJwtProvider {
    private final JwtUtil jwtUtil;

    public TestJwtProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createTestAccessToken(String email, String role) {
        return jwtUtil.createJwt("access", 1L ,email, role, 3600000L); // 1시간
    }

    public String createTestRefreshToken(String email, String role) {
        return jwtUtil.createJwt("refresh", 1L, email, role, 86400000L); // 24시간
    }
}
