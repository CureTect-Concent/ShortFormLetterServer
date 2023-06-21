package com.shotFormLetter.sFL.domain.member.controller;

import com.shotFormLetter.sFL.domain.member.dto.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.TokenUser;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody MemberDto memberDto) {
        memberService.join(memberDto);
        String message = "Okay!";
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"" + message + "\"}");
    }


    @PostMapping("/login")
    public TokenUser login(@Valid @RequestBody LoginDto loginDto) {
        TokenUser tokenUser=memberService.login(loginDto);
        return tokenUser;
    }
}
