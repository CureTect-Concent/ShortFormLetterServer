package com.shotFormLetter.sFL.domain.member.controller;

import com.shotFormLetter.sFL.domain.member.dto.*;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;

import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;;
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
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto memberDto) {

        MessageDto messageDto = new MessageDto();
        memberService.join(memberDto);
        messageDto.setMessage("회원 가입이 완료되었습니다");
        return ResponseEntity.ok(messageDto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        TokenUser tokenUser=memberService.login(loginDto);
        return ResponseEntity.ok(tokenUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getInfo(@RequestHeader("X-AUTH-TOKEN")String token){

        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(memberService.getUserInfo(tokenMember));
    }
    @PutMapping("/changeProfile")
    public ResponseEntity<?> profile(@RequestParam(value = "userProfileImage",required = false)MultipartFile userProfileImage,
                                     @RequestParam(value ="userName",required = false, defaultValue = "null")String userName,
                                     @RequestParam(value = "isDelete") Boolean isDelete,
                                     @RequestHeader("X-AUTH-TOKEN")String token) {

        MessageDto messageDto=new MessageDto();

        if(userProfileImage==null && userName.equals("null")){
            messageDto.setMessage("변경 사항 없음");
            return ResponseEntity.ok(messageDto);
        }

        Member tokenMember=memberService.tokenMember(token);
        memberService.change(tokenMember,userProfileImage,userName,isDelete);
        messageDto.setMessage("수정이 완료되었습니다");
        return ResponseEntity.ok(messageDto);
    }


    @PostMapping("/change-accesstoken")
    public ResponseEntity<?> updateAccessToken(@RequestHeader("X-REFRESH-TOKEN")String refreshToken){
        NewAccessToken newAccessToken= memberService.newAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/change-refreshtoken")
    public ResponseEntity<?> updateRefreshToken(@RequestHeader("X-AUTH-TOKEN")String accessToken){

        NewRefreshToken newRefreshToken= memberService.newRefreshToken(accessToken);
        return ResponseEntity.ok(newRefreshToken);
    }
}



