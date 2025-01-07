package com.mars.NangPaGo.provider;

import com.mars.NangPaGo.common.util.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class TestJwtProvider {
    private final JwtUtil jwtUtil;

    public TestJwtProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createTestAccessToken(String email, String role) {
        return jwtUtil.createJwt("access", email, role, 3600000L); // 1시간
    }

    public String createTestRefreshToken(String email, String role) {
        return jwtUtil.createJwt("refresh", email, role, 86400000L); // 24시간
    }
}
