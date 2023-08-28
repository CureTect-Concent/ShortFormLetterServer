package com.shotFormLetter.sFL.domain.member.token;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotMatchException;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${JWTSECRETKEY}")
    private String secretKey;

    private long accessTokenValidTime = 24 * 60 * 60 * 1000L;
    private long refreshTokenValidTime = 14 * 24 * 60 * 60 * 1000L;


    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 생성
    public String createToken(String userPk, List<String> roles) {  // userPK = email
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // 토큰 유효시각 설정
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 암호화 알고리즘과, secret 값
                .compact();
    }

    // Refresh Token 생성 // 새로 추가된놈
    public String createRefreshToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보 단위
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime)) // 토큰 유효시각 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘과 secret 값
                .compact();
    }


    // 인증 정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        String k= Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//        Optional<Member> optionalMember = memberRepository.findByUserId(k);
//        if(optionalMember.get()==null){
//            throw new DataNotMatchException("해당토큰은 없는 유저의 토큰 입니다");
//        }
        System.out.println(k);
        return k;
    }

    // 토큰 유효성, 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            System.out.println(claims);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


    // 추가 된놈
    public boolean validateRefreshToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkRefreshToken(String refreshToken, LocalDateTime localDateTime) {
        Date refreshTokenExpiration = getRefreshTokenExpiration(refreshToken);

        if (refreshTokenExpiration != null) {
            LocalDateTime refreshTokenExpirationDateTime = refreshTokenExpiration.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime oneDayBeforeExpiration = refreshTokenExpirationDateTime.minusDays(1);
            return localDateTime.isBefore(refreshTokenExpirationDateTime) && localDateTime.isAfter(oneDayBeforeExpiration);
        }
        return false;
    }

    public Date getRefreshTokenExpiration(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
            return claims.getBody().getExpiration();
        } catch (Exception e) {
            // 예외 처리 로직 작성
            throw new DataNotMatchException("삭제된 토큰 입니다");
        }
    }



    // 추가 된놈
    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("X-REFRESH-TOKEN");
    }
    // Request의 Header에서 token 값 가져오기
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }
}