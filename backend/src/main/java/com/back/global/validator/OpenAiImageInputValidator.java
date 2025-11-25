package com.back.global.validator;

import com.back.global.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class OpenAiImageInputValidator {

    private static final long MAX_TOTAL_SIZE = 50L * 1024 * 1024; // 50MB
    private static final int MAX_IMAGE_COUNT = 500;

    private static final List<String> SUPPORTED_CONTENT_TYPES = List.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp",
            "image/gif"
    );

    public static void validateImages(List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "이미지가 비어 있습니다.");
        }

        if (images.size() > MAX_IMAGE_COUNT) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "이미지는 최대 500개까지 업로드할 수 있습니다.");
        }

        long totalSize = 0;

        for (MultipartFile image : images) {

            if (image.isEmpty()) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, "비어있는 이미지 파일이 포함되어 있습니다.");
            }

            String contentType = image.getContentType();

            if (contentType == null || !SUPPORTED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
                throw new ServiceException(
                        HttpStatus.BAD_REQUEST,
                        "지원하지 않는 이미지 형식입니다. PNG, JPEG, WEBP, GIF 파일만 업로드 가능합니다."
                );
            }

            totalSize += image.getSize();
        }

        if (totalSize > MAX_TOTAL_SIZE) {
            throw new ServiceException(
                    HttpStatus.BAD_REQUEST,
                    "전체 이미지 용량이 50MB를 초과합니다."
            );
        }
    }
}
