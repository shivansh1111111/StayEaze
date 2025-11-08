package com.business.integration.aws.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${file.upload.allowed-extensions}")
    private String allowedExtensions;

    @Value("${file.upload.max-file-size}")
    private long maxFileSize;

    /**
     * Upload file to S3 bucket
     */
    private String uploadToS3(MultipartFile file) {
        String fileName = generateFileName(file);

        try (InputStream inputStream = file.getInputStream()) {

            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.setCacheControl("max-age=31536000"); // Cache for 1 year

            // Upload to S3
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    inputStream,
                    metadata
            );
            //.withCannedAcl(CannedAccessControlList.PublicRead); // Make file publicly readable

            amazonS3.putObject(putObjectRequest);

            // Return public URL
            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();

            log.info("File uploaded to S3: {}", fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("Error reading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to read file", e);
        } catch (AmazonServiceException e) {
            log.error("AWS S3 error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload to S3: " + e.getMessage(), e);
        }
    }

    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB",
                            maxFileSize / (1024 * 1024)));
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        List<String> allowedExtensionsList = Arrays.asList(allowedExtensions.split(","));

        if (!allowedExtensionsList.contains(extension)) {
            throw new IllegalArgumentException(
                    String.format("File type not allowed. Allowed types: %s", allowedExtensions));
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        log.debug("File validation passed: {}", originalFilename);
    }

    /**
     * Generate unique file name
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // Generate unique filename: timestamp_uuid.extension
        return System.currentTimeMillis() + "_" + UUID.randomUUID() + "." + extension;
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Extract S3 key from URL
     */
    private String extractS3Key(String fileUrl) {
        // URL format: https://bucket-name.s3.region.amazonaws.com/filename
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * Delete file from S3 and database
     */

    public void deleteFile(String fileUrl) {

        try {
            // Delete from S3
            amazonS3.deleteObject(bucketName, extractS3Key(fileUrl));
            log.info("File deleted from S3: {}", fileUrl);

        } catch (AmazonServiceException e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public List<String> uploadMultipleFiles(List<MultipartFile> photos) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : photos) {
            fileUrls.add(uploadToS3(file));
        }
        return fileUrls;
    }
}
