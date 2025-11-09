package com.business.booking.controllers;

import com.business.booking.dto.FileUploadResponse;
import com.business.booking.dto.PhotoUploadByLinkRequestDto;
import com.business.booking.dto.PhotoUploadRequestDto;
import com.business.booking.service.DocumentManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
    @RestController
    @RequestMapping(path = "/businessCompany")
    public class DocumentController {
        @Autowired
        DocumentManagerService documentManagerService;
        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<FileUploadResponse> uploadPhotos(@ModelAttribute PhotoUploadRequestDto requestDto) {
            FileUploadResponse response = documentManagerService.uploadFiles(requestDto.getPhotos());

            // Return list of uploaded filenames (just like frontend expects)
            return ResponseEntity.ok(response);
        }

        @PostMapping(value = "/uploadByLink")
        public ResponseEntity<String> uploadPotoByLink(@RequestBody PhotoUploadByLinkRequestDto requestDto) {
            log.debug("inside uploadByLink handler");
            return ResponseEntity.ok( documentManagerService.uploadFileUsingLink(requestDto.getImageUrl()));
        }

        @PostMapping("/download")
        public MultipartFile downloadImage(@RequestBody PhotoUploadByLinkRequestDto requestDto) {
            return documentManagerService.downloadFile(requestDto.getImageUrl());
        }
    }
