package com.mars.NangPaGo.support;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
public abstract class AbstractRepositoryTestSupport {
    @BeforeAll
    static void setUp() {
        // 테스트 시작 전 기존 Firebase 인스턴스 정리
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }
}
