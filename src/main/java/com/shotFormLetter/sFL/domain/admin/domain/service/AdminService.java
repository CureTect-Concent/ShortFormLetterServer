package com.shotFormLetter.sFL.domain.admin.domain.service;


import com.shotFormLetter.sFL.ExceptionHandler.DataNotAccessException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotMatchException;
import com.shotFormLetter.sFL.domain.admin.controller.dto.request.BenUserForm;
import com.shotFormLetter.sFL.domain.admin.domain.entity.Admin;
import com.shotFormLetter.sFL.domain.admin.domain.repository.AdminRepository;
import com.shotFormLetter.sFL.domain.member.dto.request.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.request.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.response.TokenUser;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.token.JwtTokenProvider;
import com.shotFormLetter.sFL.domain.report.domain.entity.Report;
import com.shotFormLetter.sFL.domain.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void join(MemberDto memberDto){
        memberDto.validate();
        Admin existingMember = adminRepository.findByAdminUserId(memberDto.getUserId());
        if (existingMember!=null) {
            throw new DataNotFoundException("이미 사용 중인 아이디입니다");
        }
        Admin existingNickname = adminRepository.findByAdminName(memberDto.getUserName());
        if (existingNickname!=null) {
            throw new DataNotFoundException("이미 사용 중인 닉네임입니다");
        }
        Admin admin = Admin.builder()
                .adminUserId(memberDto.getUserId())
                .adminName(memberDto.getUserName())
                .adminPassword(passwordEncoder.encode(memberDto.getPassword()))
                .adminRoles(Collections.singletonList("ROLE_ADMIN"))
                .build();

        Member member = Member.builder()
                .userId(memberDto.getUserId())
                .userName(memberDto.getUserName())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .isBend(true)
                .build();

        memberRepository.save(member);
        adminRepository.save(admin);
    }

    @Transactional
    public TokenUser login(LoginDto loginDto){
        loginDto.validate();
        Admin admin = adminRepository.findByAdminUserId(loginDto.getUserId());
        if(admin == null){
            throw new DataNotMatchException("계정 정보 없음");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), admin.getAdminPassword())) {
            throw new DataNotMatchException("잘못된 비밀번호입니다");
        }

        String accessToken = jwtTokenProvider.createToken(admin.getAdminUserId(), admin.getAdminRoles());
        String refreshToken= jwtTokenProvider.createRefreshToken(admin.getAdminName());
        TokenUser tokenUser =new TokenUser();
        tokenUser.setAccessToken(accessToken);
        tokenUser.setRefreshToken(refreshToken);
        tokenUser.setExpiredTokenTime(jwtTokenProvider.getRefreshTokenExpiration(refreshToken));
        admin.setAdminRefreshToken(refreshToken);
        adminRepository.save(admin);
        return tokenUser;
    }

    public void userBen(BenUserForm benUserForm, String token){
        checkValidAdmin(token);
        Report report = reportRepository.findByReportId(benUserForm.getReportId());
        Member member =report.getReportMember();
        if (member == null){
            throw new DataNotFoundException("삭제된 아이디 입니다");
        }
        member.setIsBend(false);
        report.setReportStatus("처리");
        memberRepository.save(member);
    }

    public void unLockUser(BenUserForm benUserForm, String token){
        checkValidAdmin(token);
        Report report = reportRepository.findByReportId(benUserForm.getReportId());
        Member member =report.getReportMember();
        if (member == null){
            throw new DataNotFoundException("삭제된 아이디 입니다");
        }
        member.setIsBend(true);
        report.setReportStatus("해제");
        memberRepository.save(member);
    }

    public void changeReportStatus(BenUserForm benUserForm,String token){
        checkValidAdmin(token);
        Report report =reportRepository.findByReportId(benUserForm.getReportId());
        report.setReportStatus("처리중");
        reportRepository.save(report);
    }

    public void checkValidAdmin(String token){
        String AdminId=jwtTokenProvider.getUserPk(token);
        Admin admin = adminRepository.findByAdminUserId(AdminId);
        if(admin == null){
            throw new DataNotAccessException("접근 권한 없음");
        }
        if(admin.getAdminRoles().equals("ROLE_USER")){
            throw new DataNotAccessException("관리자 권한이 없습니다");
        }
    }

}
