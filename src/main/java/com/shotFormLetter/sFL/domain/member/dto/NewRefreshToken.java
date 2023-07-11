package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NewRefreshToken {
    private String newRefreshToken;
    private LocalDateTime expiredTokenTime;
}
