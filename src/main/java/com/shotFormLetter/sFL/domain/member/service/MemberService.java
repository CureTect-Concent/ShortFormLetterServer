package com.shotFormLetter.sFL.domain.member.service;


import com.shotFormLetter.sFL.domain.member.dto.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.TokenUser;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(MemberDto memberDto){
        Optional<Member> existingMember = memberRepository.findByUserId(memberDto.getUserId());
        if (existingMember.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        Optional<Member> existingNickname = memberRepository.findByUserName(memberDto.getUserName());
        if (existingNickname.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        Member member = Member.builder()
                .userId(memberDto.getUserId())
                .userName(memberDto.getUserName())
                .roles(Collections.singletonList("ROLE_USER"))
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build();
        return memberRepository.save(member).getId();
    }

    @Transactional
    public TokenUser login(LoginDto loginDto){
        Member member = memberRepository.findByUserId(loginDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID 입니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        System.out.println(member.getUsername());
        String token = jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
        TokenUser tokenUser =new TokenUser();
        tokenUser.setToken(token);
        tokenUser.setUserName(member.getUserId());
        tokenUser.setId(member.getId());
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
}

