package com.back.domain.member.controller;

import com.back.domain.member.dto.MemberDto;
import com.back.domain.member.service.MemberService;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/adm/members")
public class MemberAdmController implements MemberAdmApi {
    private final MemberService memberService;

    @PatchMapping("/{id}/ban")
    public ResponseEntity<RsData<MemberDto>> banMember(
            @PathVariable Long id
    ) {
        MemberDto memberDto = memberService.banMember(id);
        RsData<MemberDto> response = new RsData<>(HttpStatus.OK, "회원이 제재되었습니다.", memberDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/unban")
    public ResponseEntity<RsData<MemberDto>> unbanMember(
            @PathVariable Long id
    ) {
        MemberDto memberDto = memberService.unbanMember(id);
        RsData<MemberDto> response = new RsData<>(HttpStatus.OK, "회원 제재가 해제되었습니다.", memberDto);
        return ResponseEntity.ok(response);
    }
}
