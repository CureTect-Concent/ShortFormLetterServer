package com.shotFormLetter.sFL.domain.member.service;


import com.shotFormLetter.sFL.ExceptionHandler.*;
import com.shotFormLetter.sFL.domain.member.dto.request.ChargeDto;
import com.shotFormLetter.sFL.domain.member.dto.request.DeleteTokenDto;
import com.shotFormLetter.sFL.domain.member.dto.request.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.request.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.response.*;
import com.shotFormLetter.sFL.domain.member.dto.validation.EmojiDto;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.token.JwtTokenProvider;
import com.shotFormLetter.sFL.domain.post.domain.dto.response.MessageDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;

import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final s3UploadService s3UploadService;
    private final PostRepository postRepository;

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
                .refreshToken(null)
                .isBend(true)
                .adsStatus(false)
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
        if(member.getIsBend()==null || member.getIsBend().equals("null")){
            String accessToken = jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
            String refreshToken= jwtTokenProvider.createRefreshToken(member.getUsername());
            TokenUser tokenUser =new TokenUser();
            tokenUser.setAccessToken(accessToken);
            tokenUser.setRefreshToken(refreshToken);
            tokenUser.setExpiredTokenTime(jwtTokenProvider.getRefreshTokenExpiration(refreshToken));
            member.setRefreshToken(refreshToken);
            member.setIsBend(true);
            member.setAdsStatus(false);
            memberRepository.save(member);
            return tokenUser;
        }
        if(member.getIsBend()==false){
            throw new DataNotAccessException("정지된 회원입니다");
        }
        String accessToken = jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
        String refreshToken= jwtTokenProvider.createRefreshToken(member.getUsername());
        TokenUser tokenUser =new TokenUser();
        tokenUser.setAccessToken(accessToken);
        tokenUser.setRefreshToken(refreshToken);
        tokenUser.setExpiredTokenTime(jwtTokenProvider.getRefreshTokenExpiration(refreshToken));
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);
        return tokenUser;
    }

    public Member tokenMember(String token){

        String userId=jwtTokenProvider.getUserPk(token);
        Optional<Member> optionalMember =memberRepository.findByUserId(userId);
        Member member = optionalMember.orElseThrow(() -> new DataNotAccessException("유저 정보가 없습니다"));
        if (!member.getIsBend()) {
            member.setRefreshToken(null);
            memberRepository.save(member);
            throw new UnauthorizedException("정지된 유저 입니다");
        }
        return optionalMember.get();
    }


    public NewAccessToken newAccessToken(String refreshToken){
        if(jwtTokenProvider.validateRefreshToken(refreshToken)==Boolean.FALSE){
            throw new DataNotAccessException("토큰 만료");
        }
        String userId = jwtTokenProvider.getUserPk(refreshToken);
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (optionalMember.get().getRefreshToken()==null || !(optionalMember.get().getRefreshToken().equals(refreshToken))) {
            throw new DataNotFoundException("삭제된 토큰 입니다");
        }
        String getAccessToken = jwtTokenProvider.createToken(userId,optionalMember.get().getRoles());
        NewAccessToken newAccessToken= new NewAccessToken();
        newAccessToken.setNewAccessToken(getAccessToken);
        return newAccessToken;
    }


    public NewRefreshToken newRefreshToken(String accessToken, String refreshToken , LocalDateTime localDateTime){
        if(jwtTokenProvider.validateToken(accessToken)==Boolean.FALSE){
            throw new UnauthorizedException("토큰 만료");
        }
        String userId=jwtTokenProvider.getUserPk(accessToken);
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (!optionalMember.isPresent()) {
            throw new DataNotFoundException("찾을 수 없음");
        }
        Member member=memberRepository.getById(optionalMember.get().getId());
        if(!member.getRefreshToken().equals(refreshToken)){
            throw new DataNotFoundException("삭제된 토큰입니다");
        } else if(jwtTokenProvider.checkRefreshToken(refreshToken,localDateTime)==Boolean.FALSE){
            throw new DataNotAccessException("자동 토큰 갱신 시간이 아닙니다");
        }
        String getrefreshToken= jwtTokenProvider.createRefreshToken(userId);
        NewRefreshToken newRefreshToken=new NewRefreshToken();
        newRefreshToken.setNewRefreshToken(getrefreshToken);
        newRefreshToken.setExpiredTokenTime(jwtTokenProvider.getRefreshTokenExpiration(getrefreshToken));
        optionalMember.get().setRefreshToken(getrefreshToken);
        memberRepository.save(optionalMember.get());
        return newRefreshToken;
    }

    public ResponseEntity<?> deleteToken(DeleteTokenDto deleteTokenDto){
        Optional<Member> deleteTokenMember=memberRepository.findByUserId(deleteTokenDto.getUserId());
        if(!deleteTokenMember.isPresent()){
            throw new DataNotFoundException("등록된 회원이 아닙니다");
        }
        Member member =Member.builder()
                .id(deleteTokenMember.get().getId())
                .refreshToken(null)
                .build();
        memberRepository.save(member);
        MessageDto messageDto=new MessageDto();
        messageDto.setMessage("토큰 삭제 완료 다시 로그인 해주세요");
        return ResponseEntity.ok(messageDto);
    }


    public String getUserIdFromMember(Member member) {
        return member.getUsername();
    }

    public UserInfo getUserInfo(Member tokenMember){
        UserInfo userInfo=new UserInfo();
        userInfo.setUserName(tokenMember.getUserId());
        userInfo.setUserSeq(tokenMember.getId());
        userInfo.setUserProfile(tokenMember.getProfile());
        userInfo.setAdsStatus(tokenMember.getAdsStatus());
        return userInfo;
    }

    public void change(Member member,MultipartFile userImageFile, String userName,Boolean isDelete){
        String link=member.getProfile();
        EmojiDto emojidto=new EmojiDto();

        //기본이미지 보낸경우
        if(userImageFile==null) {
            // 기존 이미지가 있는 상태에서 깡통으로 바꾸고싶다면?
            if(link!=null && isDelete){
                s3UploadService.deleteUserImage(link);
                link=null;
            }
        } else{
            String checkfile=StringUtils.getFilenameExtension(userImageFile.getOriginalFilename());
            List<String> chekList= Arrays.asList("jpg", "png", "jpeg", "gif");
            if(!chekList.contains(checkfile.toLowerCase())){
                throw new DataNotUploadException("이미지가 아닌 파일은 올릴 수 없습니다");
            }
            // 기존 이미지가 있는 상태에서 새로운 이미지를 바꾸고샆다면?
//            if(link!=null && isDelete==Boolean.FALSE){
//                s3UploadService.deleteUserImage(link);
//                link=s3UploadService.uploadProfile(userImageFile,member);
//            } else{
//                link=s3UploadService.uploadProfile(userImageFile,member);
//            }
            if(link != null && !isDelete){
                s3UploadService.deleteUserImage(link);
            }
            link=s3UploadService.uploadProfile(userImageFile,member);
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


    public void deleteUser(String accessToken, DeleteUserDto deleteUserDto){
        String userId=jwtTokenProvider.getUserPk(accessToken);
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (!optionalMember.isPresent()) {
            throw new DataNotFoundException("찾을 수 없음");
        }
        if(!optionalMember.get().getId().equals(deleteUserDto.getUserSeq())){
            throw new DataNotAccessException("접근 권한이 없습니다");
        }
        List<Post> posts=postRepository.getPostByUserId(userId);
        for(Post post:posts){
            List<String> s3urls = post.getS3Urls();
            String postId = post.getPostId().toString();
            s3UploadService.deleteList(s3urls);
            postRepository.delete(post);
        }
        if(optionalMember.get().getProfile()!=null){
            s3UploadService.deleteUserImage(optionalMember.get().getProfile());
        }
        memberRepository.delete(optionalMember.get());
    }

    public void charge(ChargeDto chargeDto){
        Long userSeq = chargeDto.getUserSeq();
        System.out.println("받은 userSeq : " + userSeq);
        Member AdsMember = memberRepository.getById(userSeq);
        if(AdsMember==null){
            throw new DataNotFoundException("찾을 수 없음");
        }
        AdsMember.setAdsStatus(true);
        memberRepository.save(AdsMember);

    }
}

