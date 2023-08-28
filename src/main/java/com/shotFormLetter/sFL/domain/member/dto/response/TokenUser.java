package com.shotFormLetter.sFL.domain.member.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class TokenUser {
    private String accessToken;
    private String refreshToken;
    private Date expiredTokenTime;
}