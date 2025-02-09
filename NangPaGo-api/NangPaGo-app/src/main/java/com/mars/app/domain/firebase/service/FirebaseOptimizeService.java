package com.mars.app.domain.firebase.service;

import static com.google.cloud.storage.Acl.Role.READER;
import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR_FILE_NOT_FOUND;
import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR_IMAGE_UPLOAD;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FirebaseOptimizeService {

    private final Bucket bucket;

    private static final int TARGET_WIDTH = 960;
    private static final int TARGET_HEIGHT = 630;
    private static final float IMAGE_QUALITY = 0.8f;

    private static final String ORIGINAL_PATH = "original/";
    private static final String OPTIMIZED_PATH = "optimized/";
    private static final String IMAGE_URL_PREFIX = "https://storage.googleapis.com/";
    private static final String IMAGE_CONTENT_TYPE = "image/";
    private static final String VALID_IMAGE_EXTENSIONS = "jpg|jpeg|png|webp|gif";
    private static final String URL_CLEANUP_REGEX = "^(https?://.*/)?" + ORIGINAL_PATH;

    public String optimizeAndUpload(String originalFileUrl) {
        String originalFileName = extractFileNameFromUrl(originalFileUrl);
        Blob oldBlob = getBlobFromStorage(originalFileName);
        byte[] imageBytes = oldBlob.getContent();

        BufferedImage image = loadImage(imageBytes, originalFileName);
        String extension = getFileExtension(originalFileName);
        String newFileName = OPTIMIZED_PATH + originalFileName.replaceFirst("^" + ORIGINAL_PATH, "");

        if (isSmallImage(image)) {
            return saveAndReturnUrl(newFileName, imageBytes, extension);
        }

        byte[] optimizedImage = optimizeImage(image, extension);
        return saveAndReturnUrl(newFileName, optimizedImage, extension);
    }

    private Blob getBlobFromStorage(String fileName) {
        Blob blob = bucket.get(fileName);
        validateBlobExists(blob, fileName);
        return blob;
    }

    private String saveAndReturnUrl(String fileName, byte[] data, String extension) {
        saveToStorage(fileName, data, extension);
        return generateFileUrl(fileName);
    }

    private void saveToStorage(String fileName, byte[] data, String extension) {
        Blob blob = bucket.create(fileName, data, IMAGE_CONTENT_TYPE + extension);
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), READER));
    }

    private BufferedImage loadImage(byte[] imageBytes, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw SERVER_ERROR_IMAGE_UPLOAD.of("이미지를 불러오는 중 오류 발생");
            }
            return image;
        } catch (IOException e) {
            throw SERVER_ERROR_IMAGE_UPLOAD.of("이미지를 불러오는 중 오류 발생");
        }
    }

    private byte[] optimizeImage(BufferedImage image, String extension) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
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

    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.replaceAll(URL_CLEANUP_REGEX, ORIGINAL_PATH);
    }

    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(f.lastIndexOf('.') + 1).toLowerCase())
            .filter(this::isValidImageExtension)
            .orElse("jpg");
    }

    private boolean isValidImageExtension(String extension) {
        return extension.matches(VALID_IMAGE_EXTENSIONS);
    }

    private String generateFileUrl(String fileName) {
        return IMAGE_URL_PREFIX + bucket.getName() + "/" + fileName;
    }

    private void validateBlobExists(Blob blob, String fileName) {
        if (isInvalidBlob(blob)) {
            throw SERVER_ERROR_FILE_NOT_FOUND.of("Firebase Storage에서 파일을 찾을 수 없습니다. ");
        }
    }

    private boolean isInvalidBlob(Blob blob) {
        return blob == null || blob.getContent() == null;
    }
}
