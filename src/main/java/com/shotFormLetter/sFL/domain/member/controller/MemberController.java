package com.shotFormLetter.sFL.domain.member.controller;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.dto.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.TokenUser;
import com.shotFormLetter.sFL.domain.member.dto.UserInfo;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.PostInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto memberDto) {
        MessageDto messageDto=new MessageDto();
        try {
            memberService.join(memberDto);
            messageDto.setMessage("회원 가입이 완료되었습니다");
            return ResponseEntity.ok(messageDto);
        } catch (DataNotFoundException e) {
            messageDto.setMessage(e.getMessage());
            messageDto.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(messageDto);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        MessageDto messageDto=new MessageDto();
        try {
            TokenUser tokenUser=memberService.login(loginDto);
            return ResponseEntity.ok(tokenUser);
        } catch (DataNotFoundException e){
            messageDto.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getInfo(@RequestHeader("X-AUTH-TOKEN")String token){
        Member tokenMember=memberService.tokenMember(token);
        MessageDto messageDto=new MessageDto();
        if (tokenMember==null) {
            messageDto.setMessage("권한 없음");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
        return ResponseEntity.ok(memberService.getUserInfo(tokenMember));
    }
    @PutMapping("/changeProfile")
    public ResponseEntity<?> profile(@RequestParam(value = "userProfileImage",required = false)MultipartFile userProfileImage,
                                     @RequestParam(value ="userName",required = false, defaultValue = "null")String userName,
                                     @RequestParam(value = "isDelete") Boolean isDelete,
                                     @RequestHeader("X-AUTH-TOKEN")String token) {

        Member tokenMember=memberService.tokenMember(token);
        MessageDto messageDto=new MessageDto();


        if(userProfileImage==null && userName.equals("null")){
            messageDto.setMessage("변경 사항 없음");
            return ResponseEntity.ok(messageDto);
        }

        try{
            memberService.change(tokenMember,userProfileImage,userName,isDelete);
            messageDto.setMessage("수정이 완료되었습니다");
            return ResponseEntity.ok(messageDto);
        } catch (DataNotFoundException e){
            messageDto.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
    }

    @DeleteMapping("/userimage")
    public ResponseEntity<?> profile(@RequestHeader("X-AUTH-TOKEN")String token){
        Member tokenMember=memberService.tokenMember(token);
        MessageDto messageDto=new MessageDto();
        try{
            memberService.deleteUserImage(tokenMember);
            messageDto.setMessage("기본이미지로 설정되었습니다.");
            return ResponseEntity.ok(messageDto);
        }catch (DataNotFoundException e){
            messageDto.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
    }
}



//        try {
//                memberService.join(memberDto);
//                messageDto.setMessage("회원 가입이 완료되었습니다");
//                return ResponseEntity.ok(messageDto);
//                } catch (DataNotFoundException e) {
//                messageDto.setMessage(e.getMessage());
//                messageDto.setMessage(e.getMessage());
//                return ResponseEntity.badRequest().body(messageDto);
//                }
