package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TokenUser {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiredTokenTime;
}