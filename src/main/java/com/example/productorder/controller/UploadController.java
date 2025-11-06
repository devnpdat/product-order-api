package com.example.productorder.controller;

import com.example.productorder.dto.ImageUploadResponseDTO;
import com.example.productorder.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Upload", description = "File Upload API")
public class UploadController {

    private final S3Service s3Service;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload product image", description = "Upload an image file to S3 and return the URL")
    public ResponseEntity<?> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            // Check if S3 is enabled
            if (!s3Service.isS3Enabled()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ImageUploadResponseDTO(
                                null,
                                "S3 is not enabled or configured. Please configure AWS credentials in application.properties",
                                null
                        ));
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ImageUploadResponseDTO(null, "File is empty", null));
            }

            // Check file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest()
                        .body(new ImageUploadResponseDTO(null, "File size exceeds 5MB limit", null));
            }

            // Upload to S3
            String imageUrl = s3Service.uploadFile(file, "products");

            log.info("Image uploaded successfully: {}", imageUrl);

            return ResponseEntity.ok(new ImageUploadResponseDTO(
                    imageUrl,
                    "Image uploaded successfully",
                    file.getOriginalFilename()
            ));

        } catch (IllegalArgumentException e) {
            log.error("Invalid file upload request", e);
            return ResponseEntity.badRequest()
                    .body(new ImageUploadResponseDTO(null, e.getMessage(), null));

        } catch (IOException e) {
            log.error("Failed to upload image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImageUploadResponseDTO(null, "Failed to upload image: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/image")
    @Operation(summary = "Delete product image", description = "Delete an image from S3 by URL")
    public ResponseEntity<?> deleteProductImage(@RequestParam("url") String imageUrl) {
        try {
            if (!s3Service.isS3Enabled()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("S3 is not enabled or configured");
            }

            s3Service.deleteFile(imageUrl);
            return ResponseEntity.ok("Image deleted successfully");

        } catch (Exception e) {
            log.error("Failed to delete image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete image: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Check S3 status", description = "Check if S3 is enabled and configured")
    public ResponseEntity<?> getS3Status() {
        boolean enabled = s3Service.isS3Enabled();
        return ResponseEntity.ok(new Object() {
            public final boolean s3Enabled = enabled;
            public final String message = enabled
                    ? "S3 is enabled and ready"
                    : "S3 is not enabled or not configured properly";
        });
    }

    @GetMapping("/image/{folder}/{filename}")
    @Operation(summary = "Get image", description = "Serve image file from S3 through backend proxy")
    public ResponseEntity<Resource> getImage(
            @PathVariable String folder,
            @PathVariable String filename) {
        try {
            if (!s3Service.isS3Enabled()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            // Get file from S3
            byte[] imageBytes = s3Service.downloadFile(folder + "/" + filename);

            // Determine content type
            String contentType = determineContentType(filename);

            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to get image: {}/{}", folder, filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/image/presigned")
    @Operation(summary = "Get presigned URL", description = "Generate temporary presigned URL for direct S3 access")
    public ResponseEntity<?> getPresignedUrl(
            @RequestParam String key,
            @RequestParam(defaultValue = "3600") int expirationSeconds) {
        try {
            if (!s3Service.isS3Enabled()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("S3 is not enabled or configured");
            }

            String presignedUrl = s3Service.generatePresignedUrl(key, expirationSeconds);

            return ResponseEntity.ok(new Object() {
                public final String url = presignedUrl;
                public final int expiresIn = expirationSeconds;
                public final String message = "Presigned URL generated successfully";
            });

        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate presigned URL: " + e.getMessage());
        }
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}

