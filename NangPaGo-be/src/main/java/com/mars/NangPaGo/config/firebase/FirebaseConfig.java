package com.mars.NangPaGo.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CONFIGURATION_FILE}") // .env에서 읽기
    private Resource configurationFile; // Resource 타입으로 설정

    @Value("${FIREBASE_BUCKET}") // .env에서 읽기
    private String bucket;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Firebase 초기화
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(configurationFile.getInputStream())) // Resource로 JSON 파일 읽기
            .setStorageBucket(bucket)
            .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public com.google.cloud.storage.Bucket firebaseBucket(FirebaseApp firebaseApp) {
        // Firebase Storage 버킷 객체 반환
        return StorageClient.getInstance(firebaseApp).bucket();
    }
}
