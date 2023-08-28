package com.shotFormLetter.sFL.domain.member.controller;

import com.shotFormLetter.sFL.domain.member.dto.request.DeleteTokenDto;
import com.shotFormLetter.sFL.domain.member.dto.request.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.request.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.request.TokenDto;
import com.shotFormLetter.sFL.domain.member.dto.response.DeleteUserDto;
import com.shotFormLetter.sFL.domain.member.dto.response.NewAccessToken;
import com.shotFormLetter.sFL.domain.member.dto.response.NewRefreshToken;
import com.shotFormLetter.sFL.domain.member.dto.response.TokenUser;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;

import com.shotFormLetter.sFL.domain.member.dto.request.RefreshTokenDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.response.MessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto memberDto) {

        MessageDto messageDto = new MessageDto();
        Long memberId=memberService.join(memberDto);
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
        Member tokenMember=memberService.tokenMember(token);
        memberService.change(tokenMember,userProfileImage,userName,isDelete);
        messageDto.setMessage("수정이 완료되었습니다");
        return ResponseEntity.ok(messageDto);
    }

    @PostMapping("/change-accesstoken")
    public ResponseEntity<?> updateAccessToken(@RequestBody RefreshTokenDto refreshTokenDto){
        NewAccessToken newAccessToken= memberService.newAccessToken(refreshTokenDto.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/change-refreshtoken")
    public ResponseEntity<?> updateRefreshToken(@RequestBody TokenDto tokenDto){
        NewRefreshToken newRefreshToken= memberService.newRefreshToken(tokenDto.getAccessToken(),tokenDto.getRefreshToken(),LocalDateTime.now());
        return ResponseEntity.ok(newRefreshToken);
    }

    @DeleteMapping("/deleteuser")
    public ResponseEntity<?> deleteUser(@RequestHeader("X-AUTH-TOKEN")String accessToken,
                                        @RequestBody DeleteUserDto deleteUserDto){

        MessageDto messageDto = new MessageDto();
        memberService.deleteUser(accessToken,deleteUserDto);
        messageDto.setMessage("회원 탈퇴가 완료되었습니다");
        return ResponseEntity.ok(messageDto);
    }

    @DeleteMapping("/delete-token")
    public ResponseEntity<?> deleteToken(@RequestBody DeleteTokenDto deleteTokenDto){
        return memberService.deleteToken(deleteTokenDto);
    }

    @GetMapping(value = "/charge", produces = MediaType.TEXT_HTML_VALUE)
    public String getPaymentPage() throws IOException {
        Resource resource = new ClassPathResource("static/pay.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    @PostMapping("/adsVisible-service")
    public void chargePoint(ChargeDto chargeDto){
        memberService.charge(chargeDto);
    }
}



