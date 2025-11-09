package com.business.integration.aws.s3.controller;
import com.business.integration.aws.s3.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aws/s3")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload multiple photos
     * POST /businessCompany/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("photos") List<MultipartFile> photos) {
        try {
            log.info("Received upload request for {} files", photos.size());

            // Upload files to S3
            List<String> uploadedUrls = fileUploadService.uploadMultipleFiles(photos);

            log.info("Successfully uploaded {} files", uploadedUrls.size());

            return ResponseEntity.ok(uploadedUrls);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to upload files: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("success", false);
        return error;
    }
}
