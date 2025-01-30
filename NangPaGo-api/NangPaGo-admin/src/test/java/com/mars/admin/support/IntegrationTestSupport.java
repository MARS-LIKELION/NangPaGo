package com.mars.admin.support;

import com.mars.admin.provider.TestJwtProvider;
import com.mars.common.util.web.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    @Autowired
    private TestJwtProvider testJwtProvider;

    @Autowired
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    protected void setAuthenticationAsUserWithToken(String email) {
        setAuthenticationWithToken(email, "ROLE_USER");
    }

    protected void setAuthenticationAsAdminWithToken(String email) {
        setAuthenticationWithToken(email, "ROLE_ADMIN");
    }

    private void setAuthenticationWithToken(String email, String role) {
        String token = testJwtProvider.createTestAccessToken(email, role);
        Authentication authentication = jwtUtil.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
