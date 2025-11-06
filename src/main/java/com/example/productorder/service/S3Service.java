package com.example.productorder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class S3Service {

    @Autowired(required = false)
    private S3Client s3Client;

    @Autowired(required = false)
    private S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name:}")
    private String bucketName;

    @Value("${aws.s3.enabled:false}")
    private boolean s3Enabled;

    @Value("${aws.s3.region:ap-southeast-1}")
    private String region;

    /**
     * Upload file to S3
     * @param file The file to upload
     * @param folder The folder path in S3 (e.g., "products", "users")
     * @return The public URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (!s3Enabled || s3Client == null) {
            throw new IllegalStateException("S3 is not enabled or configured properly");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID().toString() + extension;
        String key = folder + "/" + filename;

        try {
            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return public URL
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("File uploaded successfully to S3: {}", url);
            return url;

        } catch (S3Exception e) {
            log.error("Failed to upload file to S3", e);
            throw new IOException("Failed to upload file to S3: " + e.getMessage());
        }
    }

    /**
     * Delete file from S3
     * @param fileUrl The full URL of the file to delete
     */
    public void deleteFile(String fileUrl) {
        if (!s3Enabled || s3Client == null) {
            log.warn("S3 is not enabled, skipping file deletion");
            return;
        }

        try {
            // Extract key from URL
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                log.warn("Invalid S3 URL: {}", fileUrl);
                return;
            }

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully from S3: {}", fileUrl);

        } catch (S3Exception e) {
            log.error("Failed to delete file from S3", e);
        }
    }

    /**
     * Extract S3 key from full URL
     */
    private String extractKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Format: https://bucket-name.s3.region.amazonaws.com/folder/filename
            String[] parts = url.split(".amazonaws.com/");
            if (parts.length == 2) {
                return parts[1];
            }
        } catch (Exception e) {
            log.error("Error extracting key from URL: {}", url, e);
        }

        return null;
    }

    /**
     * Check if S3 is enabled and configured
     */
    public boolean isS3Enabled() {
        return s3Enabled && s3Client != null && !bucketName.isEmpty();
    }

    /**
     * Download file from S3
     * @param key The S3 key (e.g., "products/uuid.jpg")
     * @return File content as byte array
     */
    public byte[] downloadFile(String key) throws IOException {
        if (!s3Enabled || s3Client == null) {
            throw new IllegalStateException("S3 is not enabled or configured properly");
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            byte[] bytes = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
            log.debug("Downloaded file from S3: {}", key);
            return bytes;

        } catch (S3Exception e) {
            log.error("Failed to download file from S3: {}", key, e);
            throw new IOException("Failed to download file from S3: " + e.getMessage());
        }
    }

    /**
     * Generate presigned URL for temporary access
     * @param key The S3 key (e.g., "products/uuid.jpg")
     * @param expirationSeconds Duration in seconds (default 3600 = 1 hour)
     * @return Presigned URL
     */
    public String generatePresignedUrl(String key, int expirationSeconds) {
        if (!s3Enabled || s3Presigner == null) {
            throw new IllegalStateException("S3 is not enabled or configured properly");
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirationSeconds))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();

            log.debug("Generated presigned URL for key: {}, expires in: {} seconds", key, expirationSeconds);
            return url;

        } catch (S3Exception e) {
            log.error("Failed to generate presigned URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage());
        }
    }
}

