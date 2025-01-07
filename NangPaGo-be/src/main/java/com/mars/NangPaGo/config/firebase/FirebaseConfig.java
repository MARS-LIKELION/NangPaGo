package com.mars.NangPaGo.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CONFIGURATION_FILE}")
    private Resource configurationFile;

    @Value("${FIREBASE_BUCKET}")
    private String bucket;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            // Firebase 초기화
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                    GoogleCredentials.fromStream(configurationFile.getInputStream()))
                .setStorageBucket(bucket)
                .build();

            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public com.google.cloud.storage.Bucket firebaseBucket(FirebaseApp firebaseApp) {
        // Firebase Storage 버킷 객체 반환
        return StorageClient.getInstance(firebaseApp).bucket();
    }
}
