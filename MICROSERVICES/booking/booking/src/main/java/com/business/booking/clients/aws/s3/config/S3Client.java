package com.business.booking.clients.aws.s3.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.business.booking.exception.S3Exception;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Client {
    @Autowired
    RestTemplate restTemplate;
    @Value("${aws.s3.service.base-url}")
    private String baseUrl;
    @Value("${aws.s3.service.upload-endpoint}")
    private String endpoint;


    /**
     * Upload a single file to S3
     * @param file The file to upload
     * @return List of uploaded file URLs
     */
    public String uploadFile(MultipartFile file) {
        try {
            String url = baseUrl + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("photos", new MultipartInputStreamFileResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Uploading file: {} to S3 via endpoint: {}", file.getOriginalFilename(), url);

            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<String>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully uploaded file. URLs: {}", response.getBody());
                return response.getBody().getFirst();
            } else {
                throw new S3Exception("Failed to upload files. Status: " + response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Error uploading file to S3: {}", e.getMessage(), e);
            throw new S3Exception("Failed to upload file to S3", e);
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage(), e);
            throw new S3Exception("Failed to read file", e);
        }
    }

    /**
     * Upload multiple files to S3
     * @param files List of files to upload
     * @return List of all uploaded file URLs
     */
    public List<String> uploadFiles(List<MultipartFile> files) {
        try {
            String url = baseUrl + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for (MultipartFile file : files) {
                body.add("photos", new MultipartInputStreamFileResource(file));
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("Uploading {} files to S3 via endpoint: {}", files.size(), url);

            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<String>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully uploaded {} files. URLs: {}", files.size(), response.getBody());
                return response.getBody();
            } else {
                throw new S3Exception("Failed to upload files. Status: " + response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Error uploading files to S3: {}", e.getMessage(), e);
            throw new S3Exception("Failed to upload files to S3", e);
        } catch (IOException e) {
            log.error("Error reading files: {}", e.getMessage(), e);
            throw new S3Exception("Failed to read files", e);
        }
    }

    /**
     * Custom resource class to properly handle MultipartFile for RestTemplate
     */
    private static class MultipartInputStreamFileResource extends ByteArrayResource {

        private final String filename;

        public MultipartInputStreamFileResource(MultipartFile file) throws IOException {
            super(file.getBytes());
            this.filename = file.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
