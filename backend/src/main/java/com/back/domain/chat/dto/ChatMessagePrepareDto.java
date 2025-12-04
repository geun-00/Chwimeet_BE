package com.back.domain.chat.dto;

public record ChatMessagePrepareDto(
        Long chatMemberId,
        Long otherMemberId,
        String lastMessageSnapshot
) {
}
