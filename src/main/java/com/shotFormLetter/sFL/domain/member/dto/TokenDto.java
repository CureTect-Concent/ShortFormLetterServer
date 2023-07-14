package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;

@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
