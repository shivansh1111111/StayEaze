package com.business.booking.service.impl;

import com.business.booking.clients.aws.s3.config.S3Client;
import com.business.booking.dto.FileUploadResponse;
import com.business.booking.service.DocumentManagerService;
import com.business.booking.util.dataclasses.ByteArrayMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DocumentManagerServiceImpl implements DocumentManagerService {
    @Autowired
    S3Client s3Client;
    @Override
    public FileUploadResponse uploadFiles(List<MultipartFile> files) {
        List<String> uploadedFileUrls = s3Client.uploadFiles(files);
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setUploadedFileUrls(uploadedFileUrls);
        return fileUploadResponse;
    }

    @Override
    public MultipartFile downloadFile(String imageUrl)  {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = null;
        log.debug("imageUrl: {}",imageUrl);
        try {
            // Call the remote image URL
             response = restTemplate.exchange(
                    imageUrl,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Extract file name from URL or fallback
        String fileName = extractFileName(imageUrl);

        // Get content type
        String contentType = response.getHeaders().getContentType() != null
                ? response.getHeaders().getContentType().toString()
                : "image/jpeg";

        // Convert the byte array into MultipartFile
        MultipartFile multipartFile = new ByteArrayMultipartFile(
                response.getBody(),
                "file",
                fileName,
                contentType
        );

        return multipartFile;
    }

    @Override
    public String uploadFileUsingLink(String imageUrl) {
        log.debug("Downloading file");
        MultipartFile file = downloadFile(imageUrl);
        log.debug("Uploading file");
        return s3Client.uploadFile(file);
    }

    private String extractFileName(String url) {
        try {
            String[] parts = url.split("/");
            String namePart = parts[parts.length - 1];
            if (!namePart.contains(".")) {
                namePart += ".jpg";
            }
            return namePart.split("\\?")[0];
        } catch (Exception e) {
            return "downloaded_image.jpg";
        }
    }
}
