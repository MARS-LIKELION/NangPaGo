package com.mars.app.support;

import com.google.firebase.FirebaseApp;
import com.mars.common.model.user.User;
import com.mars.common.util.web.JwtUtil;
import com.mars.app.provider.TestJwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    @Autowired
    protected TestJwtProvider testJwtProvider;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeAll
    static void setUp() {
        // 테스트 시작 전 기존 Firebase 인스턴스 정리
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    protected void setAuthenticationAsUserWithToken(User user) {
        String token = testJwtProvider.createTestAccessToken(user);
        Authentication authentication = jwtUtil.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
