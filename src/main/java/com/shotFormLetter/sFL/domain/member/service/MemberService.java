package com.shotFormLetter.sFL.domain.member.service;


import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.dto.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.TokenUser;
import com.shotFormLetter.sFL.domain.member.dto.UserInfo;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.token.JwtTokenProvider;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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
        if (memberDto.getUserName().length()<=2){
            throw new DataNotFoundException("이름은 3글자부터 가능합니다.");
        }
        if (memberDto.getUserId().length()<5){
            throw new DataNotFoundException("Id는 5자부터 가능합니다.");
        }
        if (memberDto.getPassword().length()<6){
            throw new DataNotFoundException("비밀번호는 6자부터 가능합니다.");
        }
        Optional<Member> existingMember = memberRepository.findByUserId(memberDto.getUserId());
        if (existingMember.isPresent()) {
            throw new DataNotFoundException("이미 사용 중인 아이디입니다.");
        }
        Optional<Member> existingNickname = memberRepository.findByUserName(memberDto.getUserName());
        if (existingNickname.isPresent()) {
            throw new DataNotFoundException("이미 사용 중인 닉네임입니다.");
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
        Member member = memberRepository.findByUserId(loginDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("가입되지 않은 ID 입니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new DataNotFoundException("잘못된 비밀번호입니다.");
        }
        System.out.println(member.getUsername());
        String token = jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
        TokenUser tokenUser =new TokenUser();
        tokenUser.setToken(token);
        return tokenUser;
    }

    public Member tokenMember(String token){
        String userId = jwtTokenProvider.getUserPk(token);
        System.out.println(userId);
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (!optionalMember.isPresent()) {
            throw new IllegalStateException("User not found");
        }
        return optionalMember.get();
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

        //기본이미지 보낸경우
        if(userImageFile==null) {
            // 기존 이미지가 있는 상태에서 깡통으로 바꾸고싶다면?
            if(link!=null && isDelete==Boolean.TRUE){
                s3UploadService.deleteUserImage(link);
                link=null;
            }
        } else{
            // 기존 이미지가 있는 상태에서 새로운 이미지를 바꾸고샆다면?
            if(link!=null && isDelete==Boolean.FALSE){
                s3UploadService.deleteUserImage(link);
                link=s3UploadService.uploadProfile(userImageFile,member);
            } else{
                link=s3UploadService.uploadProfile(userImageFile,member);
            }
        }

        if(userName.equals("null")){
            throw new DataNotFoundException("이름이 없거나 null 이란 이름은 사용할 수 없습니다");
        } else {
            Member isMember= memberRepository.getByUserName(userName);
            if(isMember==null && userName.length()<2){
                throw new DataNotFoundException("이름은 2글자부터 사용가능합니다");
            } else if(isMember!=null && isMember.getId()!=member.getId()){
                throw new DataNotFoundException("중복된 이름입니다");
            }
        }
        System.out.println("이름: "+userName);
        System.out.println("사진 링크값: "+link);

        member.setProfile(link);
        member.setUserName(userName);
        memberRepository.save(member);
    }

    public void deleteUserImage(Member tokenMember){
        if (tokenMember.getProfile()==null){
            throw new DataNotFoundException("사진 정보가 없습니다.");
        }
        s3UploadService.deleteUserImage(tokenMember.getProfile());
        tokenMember.setProfile(null);
        memberRepository.save(tokenMember);
    }
}

