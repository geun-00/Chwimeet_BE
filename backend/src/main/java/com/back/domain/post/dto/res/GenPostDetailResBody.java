package com.back.domain.post.dto.res;

public record GenPostDetailResBody(
        String title,
        String content,
        Long categoryId,
        Integer fee,
        Integer deposit
) {
}
