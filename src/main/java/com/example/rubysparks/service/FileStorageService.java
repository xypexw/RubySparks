package com.example.rubysparks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final S3Client s3Client;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @PostConstruct
    public void init() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            try {
                s3Client.headBucket(headBucketRequest);
                log.info("Bucket '{}' already exists.", bucketName);
            } catch (NoSuchBucketException e) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                s3Client.createBucket(createBucketRequest);
                log.info("Successfully created bucket '{}'.", bucketName);
                
                String policy = "{\n" +
                        "    \"Version\": \"2012-10-17\",\n" +
                        "    \"Statement\": [\n" +
                        "        {\n" +
                        "            \"Sid\": \"PublicRead\",\n" +
                        "            \"Effect\": \"Allow\",\n" +
                        "            \"Principal\": \"*\",\n" +
                        "            \"Action\": [\"s3:GetObject\"],\n" +
                        "            \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";
                PutBucketPolicyRequest putBucketPolicyRequest = PutBucketPolicyRequest.builder()
                        .bucket(bucketName)
                        .policy(policy)
                        .build();
                s3Client.putBucketPolicy(putBucketPolicyRequest);
                log.info("Public read policy applied to bucket '{}'.", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize bucket '{}': {}", bucketName, e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String key = folder + "/" + UUID.randomUUID().toString() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            return endpoint + "/" + bucketName + "/" + key;
        } catch (IOException e) {
            log.error("Failed to upload file to MinIO", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
}
