package com.mars.app.domain.firebase.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.mars.app.common.exception.NPGExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FirebaseStorageService {

    private final Bucket bucket;

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            return String.format("https://storage.googleapis.com/%s/%s", bucket.getName(), fileName);
        } catch (IOException e) {
            throw NPGExceptionType.SERVER_ERROR_IMAGE_UPLOAD.of();
        }
    }
}
