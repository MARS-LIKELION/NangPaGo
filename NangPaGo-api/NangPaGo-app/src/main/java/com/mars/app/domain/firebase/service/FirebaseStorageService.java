package com.mars.app.domain.firebase.service;

import static com.google.cloud.storage.Acl.Role.READER;
import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR_FILE_DELETE;
import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR_IMAGE_UPLOAD;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {

    private final Bucket bucket;

    private static final String UPLOAD_PATH = "upload/";
    private static final String IMAGE_URL_PREFIX = "https://storage.googleapis.com/";
    private static final String IMAGE_CONTENT_TYPE = "image/";
    private static final String DEFAULT_EXTENSION = "jpg";

    private static final int TARGET_WIDTH = 960;
    private static final int TARGET_HEIGHT = 540;
    private static final float IMAGE_QUALITY = 0.8f;

    private static final Set<String> VALID_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    public String uploadNewFile(MultipartFile file) {
        if (isValidFile(file)) {
            throw SERVER_ERROR_IMAGE_UPLOAD.of("파일이 비어 있습니다.");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        byte[] optimizedImage = optimizeImage(getFileBytes(file), extension);

        return uploadToFirebase(
            optimizedImage, generateFileName(file.getOriginalFilename()), IMAGE_CONTENT_TYPE + extension);
    }

    public String updateFile(MultipartFile file, String previousImageUrl) {
        if (isValidFile(file)) {
            return previousImageUrl;
        }

        String newFileUrl = uploadNewFile(file);
        if (previousImageUrl != null && !previousImageUrl.isBlank()) {
            deleteFileFromFirebase(previousImageUrl);
        }

        return newFileUrl;
    }

    public void deleteFileFromFirebase(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String fileName = extractFileName(imageUrl);
        Blob blob = bucket.get(fileName);

        if (blob == null) {
            throw SERVER_ERROR_FILE_DELETE.of("삭제할 파일을 찾을 수 없습니다.");
        }

        try {
            blob.delete();
        } catch (Exception e) {
            throw SERVER_ERROR_FILE_DELETE.of("Firebase 파일 삭제 중 오류 발생");
        }
    }

    private boolean isValidFile(MultipartFile file) {
        return file == null || file.isEmpty();
    }
    private byte[] getFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw SERVER_ERROR_IMAGE_UPLOAD.of("파일을 읽을 수 없습니다.");
        }
    }

    private byte[] optimizeImage(byte[] imageBytes, String extension) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(inputStream);
            if (image == null || isSmallImage(image)) {
                return imageBytes;
            }

            Thumbnails.of(image)
                .size(TARGET_WIDTH, TARGET_HEIGHT)
                .outputQuality(IMAGE_QUALITY)
                .outputFormat(extension)
                .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw SERVER_ERROR_IMAGE_UPLOAD.of("이미지 최적화 중 오류 발생");
        }
    }

    private boolean isSmallImage(BufferedImage image) {
        return image.getWidth() <= TARGET_WIDTH && image.getHeight() <= TARGET_HEIGHT;
    }

    private String uploadToFirebase(byte[] fileData, String fileName, String contentType) {
        return Optional.ofNullable(bucket.create(fileName, fileData, contentType))
            .map(blob -> {
                blob.createAcl(Acl.of(Acl.User.ofAllUsers(), READER));
                return generateFileUrl(fileName);
            })
            .orElseThrow(() -> SERVER_ERROR_IMAGE_UPLOAD.of("Firebase Storage 업로드 실패"));
    }

    private String generateFileName(String originalFilename) {
        return UPLOAD_PATH + UUID.randomUUID() + "_" + extractFileNameFromUrl(originalFilename);
    }

    private String generateFileUrl(String fileName) {
        return IMAGE_URL_PREFIX + bucket.getName() + "/" + fileName;
    }

    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
            .filter(f -> f.lastIndexOf('.') != -1)
            .map(f -> f.substring(f.lastIndexOf('.') + 1))
            .filter(VALID_IMAGE_EXTENSIONS::contains)
            .orElse(DEFAULT_EXTENSION);
    }

    private String extractFileName(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return "";
        }

        int index = fileUrl.indexOf(UPLOAD_PATH);
        return (index != -1) ? fileUrl.substring(index) : fileUrl;
    }

    private String extractFileNameFromUrl(String fileName) {
        return Optional.ofNullable(fileName)
            .map(f -> f.substring(f.lastIndexOf('/') + 1))
            .orElse(UUID.randomUUID().toString());
    }
}
