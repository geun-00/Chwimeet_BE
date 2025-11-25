package com.back.domain.post.service;

import com.back.global.validator.OpenAiImageInputValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostContentGenerateService {

    private final ChatClient chatClient;

    @Value("${custom.ai.post-detail-gen-prompt}")
    private String systemPrompt;

    public String generatePostDetail(List<MultipartFile> imageFiles) {
        OpenAiImageInputValidator.validateImages(imageFiles);

        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(user -> {
                    user.text("이 사진을 기반으로 물품 대여 게시글을 작성해야하는데 해주라");
                    for (MultipartFile file : imageFiles) {
                        user.media(MimeTypeUtils.parseMimeType(file.getContentType()), file.getResource());
                    }
                })
                .call()
                .content();

        return response;
    }
}
