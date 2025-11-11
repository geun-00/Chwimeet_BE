package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.req.PostCreateReqBody;
import com.back.domain.post.post.dto.res.PostCreateResBody;
import com.back.domain.post.post.dto.res.PostDetailResBody;
import com.back.domain.post.post.dto.res.PostListResBody;
import com.back.domain.post.post.service.PostService;
import com.back.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostCreateResBody> createPost(
            @Valid @RequestBody PostCreateReqBody reqBody,
            @AuthenticationPrincipal SecurityUser user
    ) {
        Long postId = postService.createPost(reqBody, user.getId());

        PostCreateResBody response = PostCreateResBody.builder()
                .message("게시글이 등록되었습니다.")
                .postId(postId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostListResBody>> getPostList() {
        List<PostListResBody> body = postService.getPostList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResBody> getPostById(@PathVariable Long postId) {
        PostDetailResBody body = postService.getPostById(postId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PostListResBody>> getMyPostList(@AuthenticationPrincipal SecurityUser securityUser) {
        List<PostListResBody> body = postService.getMyPosts(securityUser.getId());
        return ResponseEntity.ok(body);
    }
}
