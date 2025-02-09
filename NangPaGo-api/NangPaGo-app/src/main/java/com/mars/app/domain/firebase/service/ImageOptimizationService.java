package com.mars.app.domain.firebase.service;

import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.common.model.recipe.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ImageOptimizationService {

    private final RecipeRepository recipeRepository;
    private final FirebaseOptimizeService firebaseOptimizeService;
    private final FirebaseStorageService firebaseStorageService;

    private static final int BATCH_SIZE = 100;

    @Value("${FIREBASE_CONFIGURATION_FILE}")
    private String fallbackImageUrl;

    @Transactional
    public String optimizeAndUpdateImages() {
        try {
            IntStream.iterate(0, page -> page + 1)
                .mapToObj(page -> recipeRepository.findAll(PageRequest.of(page, BATCH_SIZE)))
                .takeWhile(Page::hasContent)
                .flatMap(Page::stream)
                .forEach(this::optimizeImageWithFallback);

            return "이미지 최적화 및 업로드 완료";
        } catch (Exception e) {
            return "오류 발생: " + e.getMessage();
        }
    }

    private void optimizeImageWithFallback(Recipe image) {
        try {
            optimizeImage(image);
        } catch (IOException e) {
            applyFallbackImage(image);
        }
    }

    private void optimizeImage(Recipe image) throws IOException {
        String optimizedImageUrl = uploadAndOptimizeImage(image.getMainImage());
        updateDatabase(image, optimizedImageUrl);
    }

    private void applyFallbackImage(Recipe image) {
        updateDatabase(image, fallbackImageUrl);
    }

    private String uploadAndOptimizeImage(String imageUrl) throws IOException {
        byte[] imageBytes = downloadImage(imageUrl);
        String uploadedUrl = firebaseStorageService.uploadOriginalFile(imageBytes, imageUrl);
        return firebaseOptimizeService.optimizeAndUpload(uploadedUrl);
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        }
    }

    private void updateDatabase(Recipe image, String newImageUrl) {
        image.updateMainImage(newImageUrl);
        recipeRepository.save(image);
    }
}
