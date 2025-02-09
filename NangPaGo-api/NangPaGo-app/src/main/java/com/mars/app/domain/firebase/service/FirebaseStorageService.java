package com.mars.app.domain.firebase.service;

import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR_IMAGE_UPLOAD;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {

    private final Bucket bucket;

    private static final String ORIGINAL_PATH = "original/";
    private static final String IMAGE_URL_PREFIX = "https://storage.googleapis.com/";
    private static final String IMAGE_CONTENT_TYPE = "image/";
    public static final String DEFAULT_EXTENSION = "jpg";
    private static final Set<String> VALID_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    public String uploadFile(MultipartFile file) {
        return uploadToFirebase(
            getFileBytes(file),
            generateUniqueFileName(file.getOriginalFilename()),
            file.getContentType()
        );
    }

    public String uploadOriginalFile(byte[] imageBytes, String originalFileUrl) {
        return uploadToFirebase(
            imageBytes,
            ORIGINAL_PATH + generateUniqueFileName(originalFileUrl),
            IMAGE_CONTENT_TYPE + getValidFileExtension(originalFileUrl)
        );
    }

    private byte[] getFileBytes(MultipartFile file) {
        return Optional.ofNullable(file)
            .map(f -> {
                try {
                    return f.getBytes();
                } catch (IOException e) {
                    throw SERVER_ERROR_IMAGE_UPLOAD.of("파일을 읽을 수 없습니다.");
                }
            })
            .orElseThrow(() -> SERVER_ERROR_IMAGE_UPLOAD.of("파일이 비어 있습니다."));
    }

    private String uploadToFirebase(byte[] fileData, String fileName, String contentType) {
        return Optional.ofNullable(bucket.create(fileName, fileData, contentType))
            .map(blob -> {
                blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
                return generateFileUrl(fileName);
            })
            .orElseThrow(() -> SERVER_ERROR_IMAGE_UPLOAD.of("Firebase Storage 업로드 실패"));
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + extractFileName(originalFilename);
    }

    private String getValidFileExtension(String fileName) {
        return Optional.ofNullable(extractFileExtension(fileName))
            .filter(VALID_IMAGE_EXTENSIONS::contains)
            .orElse(DEFAULT_EXTENSION);
    }

    private String generateFileUrl(String fileName) {
        return IMAGE_URL_PREFIX + bucket.getName() + "/" + fileName;
    }

    private String extractFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(f.lastIndexOf('.') + 1))
            .orElse(null);
    }

    private String extractFileName(String fileName) {
        return Optional.ofNullable(fileName)
            .map(f -> f.substring(f.lastIndexOf('/') + 1))
            .orElse(UUID.randomUUID().toString());
    }
}
