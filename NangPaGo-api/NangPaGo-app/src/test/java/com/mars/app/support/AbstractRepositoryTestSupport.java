package com.mars.app.support;

import com.google.firebase.FirebaseApp;
import com.mars.app.support.extend.RepositoryTestCondition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//@ExtendWith(RepositoryTestCondition.class)
@ActiveProfiles("local")
@SpringBootTest
public abstract class AbstractRepositoryTestSupport {
    @BeforeAll
    static void setUp() {
        // 테스트 시작 전 기존 Firebase 인스턴스 정리
        FirebaseApp.getApps().forEach(FirebaseApp::delete);
    }
}
