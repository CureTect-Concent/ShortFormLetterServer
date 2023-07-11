package com.shotFormLetter.sFL.domain.member.service;


import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotMatchException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotUploadException;
import com.shotFormLetter.sFL.domain.member.dto.*;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.token.JwtTokenProvider;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final s3UploadService s3UploadService;

    @Transactional
    public Long join(MemberDto memberDto){
        memberDto.validate();
        Optional<Member> existingMember = memberRepository.findByUserId(memberDto.getUserId());
        if (existingMember.isPresent()) {
            throw new DataNotFoundException("이미 사용 중인 아이디입니다");
        }
        Optional<Member> existingNickname = memberRepository.findByUserName(memberDto.getUserName());
        if (existingNickname.isPresent()) {
            throw new DataNotFoundException("이미 사용 중인 닉네임입니다");
        }
        Member member = Member.builder()
                .userId(memberDto.getUserId())
                .userName(memberDto.getUserName())
                .roles(Collections.singletonList("ROLE_USER"))
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .profile(null)
                .build();
        return memberRepository.save(member).getId();
    }

    @Transactional
    public TokenUser login(LoginDto loginDto){
        loginDto.validate();
        Member member = memberRepository.findByUserId(loginDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("가입되지 않은 ID 입니다"));
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new DataNotMatchException("잘못된 비밀번호입니다");
        }
        LocalDateTime tokenTime=LocalDateTime.now();
        String accessToken = jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
        String refreshToken= jwtTokenProvider.createRefreshToken(member.getUsername());
        TokenUser tokenUser =new TokenUser();
        tokenUser.setAccessToken(accessToken);
        tokenUser.setRefreshToken(refreshToken);
        tokenUser.setExpiredTokenTime(tokenTime.plusWeeks(2));
        return tokenUser;
    }

    public Member tokenMember(String token){

        String userId=jwtTokenProvider.getUserPk(token);
        Optional<Member> optionalMember =memberRepository.findByUserId(userId);
        if(!optionalMember.isPresent()){
            throw new DataNotMatchException("권한 없읍");
        }
        return optionalMember.get();
    }

    public NewAccessToken newAccessToken(String refreshToken){
        if(jwtTokenProvider.validateRefreshToken(refreshToken)==Boolean.FALSE){
            throw new DataNotFoundException("토큰 만료");
        }
        String userId = jwtTokenProvider.getUserPk(refreshToken);
        System.out.println(userId);
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (!optionalMember.isPresent()) {
            throw new DataNotFoundException("찾을 수 없음");
        }

        String accessToken = jwtTokenProvider.createRefreshToken(userId);
        NewAccessToken newAccessToken= new NewAccessToken();
        newAccessToken.setNewAccessToken(accessToken);
        return newAccessToken;
    }

    public NewRefreshToken newRefreshToken(String accessToken){
        if(jwtTokenProvider.validateToken(accessToken)==Boolean.FALSE){
            throw new DataNotMatchException("토큰 만료");
        }
        String userId=jwtTokenProvider.getUserPk(accessToken);
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (!optionalMember.isPresent()) {
            throw new DataNotFoundException("찾을 수 없음");
        }
        LocalDateTime localDateTime=LocalDateTime.now();
        LocalDateTime expiredTokenTime=localDateTime.plusWeeks(2);
        String refreshToken= jwtTokenProvider.createRefreshToken(userId);

        NewRefreshToken newRefreshToken=new NewRefreshToken();
        newRefreshToken.setNewRefreshToken(refreshToken);
        newRefreshToken.setExpiredTokenTime(expiredTokenTime);
        return newRefreshToken;
    }


    public String getUserIdFromMember(Member member) {
        return member.getUsername();
    }

    public UserInfo getUserInfo(Member tokenMember){
        UserInfo userInfo=new UserInfo();
        userInfo.setUserName(tokenMember.getUserId());
        userInfo.setUserSeq(tokenMember.getId());
        userInfo.setUserProfile(tokenMember.getProfile());
        return userInfo;
    }

    public void change(Member member,MultipartFile userImageFile, String userName,Boolean isDelete){
        String link=member.getProfile();
        EmojiDto emojidto=new EmojiDto();
        String emoji=emojidto.getrex();

        //기본이미지 보낸경우
        if(userImageFile==null) {
            // 기존 이미지가 있는 상태에서 깡통으로 바꾸고싶다면?
            if(link!=null && isDelete==Boolean.TRUE){
                s3UploadService.deleteUserImage(link);
                link=null;
            }
        } else{
            String checkfile=StringUtils.getFilenameExtension(userImageFile.getOriginalFilename());
            if(!(checkfile.matches("jpg") || checkfile.matches("png") ||checkfile.matches("jpeg") || checkfile.matches("gif"))){
                throw new DataNotUploadException("이미지가 아닌 파일은 올릴 수 없습니다");
            }
            // 기존 이미지가 있는 상태에서 새로운 이미지를 바꾸고샆다면?
            if(link!=null && isDelete==Boolean.FALSE){
                s3UploadService.deleteUserImage(link);
                link=s3UploadService.uploadProfile(userImageFile,member);
            } else{
                link=s3UploadService.uploadProfile(userImageFile,member);
            }
        }
        if(userName.equals("null")){
            userName=member.getUserId();
        } else {
            Member isMember= memberRepository.getByUserName(userName);
            if(isMember==null && userName.length()<2){
                throw new DataNotFoundException("이름은 2글자부터 사용가능합니다");
            } else if(isMember!=null && isMember.getId()!=member.getId()){
                throw new DataNotFoundException("중복된 이름입니다");
            } else if(emojidto.findEmoji(userName)==Boolean.TRUE){
                throw new DataNotFoundException("회원 이름에 이모티콘은 사용할 수 없습니다");
            }
        }
        member.setProfile(link);
        member.setUserName(userName);
        memberRepository.save(member);
    }
}

