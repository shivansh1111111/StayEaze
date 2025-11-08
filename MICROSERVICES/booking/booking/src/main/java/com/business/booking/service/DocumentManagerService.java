package com.business.booking.service;

import com.business.booking.dto.FileUploadResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public interface DocumentManagerService {
    FileUploadResponse uploadFiles(List<MultipartFile> file);
    MultipartFile downloadFile(String imageUrl);
    String uploadFileUsingLink(String imageUrl);

}
