package com.j1p3ter.productserver.config;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidator {

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    public static boolean isImageFileValid(MultipartFile file) {
        String contentType = file.getContentType();
        return ALLOWED_FILE_TYPES.contains(contentType);
    }
}