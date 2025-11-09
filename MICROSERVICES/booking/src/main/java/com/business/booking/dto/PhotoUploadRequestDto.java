package com.business.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class PhotoUploadRequestDto {

    // Matches the field name used in FormData: "photos"
    private List<MultipartFile> photos;
}

