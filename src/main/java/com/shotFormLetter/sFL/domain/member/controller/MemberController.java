package com.shotFormLetter.sFL.domain.member.controller;

import com.shotFormLetter.sFL.domain.member.dto.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.TokenUser;
import com.shotFormLetter.sFL.domain.member.dto.UserInfo;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/profile")
    public ResponseEntity<?> getInfo(@RequestHeader("X-AUTH-TOKEN")String token){
        Member tokenMember=memberService.tokenMember(token);
        MessageDto messageDto=new MessageDto();
        if (tokenMember==null) {
            messageDto.setMessage("권한 없음");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(memberService.getUserInfo(tokenMember));
    }
    @PutMapping("/changeProfile")
    public ResponseEntity<?> profile(@RequestHeader("X-AUTH-TOKEN")String token,
                                     @RequestParam(value = "userProfileImage",required = false)MultipartFile userProfileImage,
                                     @RequestParam(value = "userName",required = false)String userName) {
        Member tokenMember=memberService.tokenMember(token);
        MessageDto messageDto=new MessageDto();
        System.out.println("dd"+userProfileImage);
        System.out.println("xx"+userName);
        if (tokenMember==null){
            messageDto.setMessage("권한 없음");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        } else if(userProfileImage==null || userName==null) {
            messageDto.setMessage("프로필 이미지 또는 변경할 닉네임을 작성해주세요");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        } else {
            memberService.changeProfile(userProfileImage,tokenMember,userName);
            messageDto.setMessage("수정완료");
        }
        return ResponseEntity.ok(messageDto);
    }
}
