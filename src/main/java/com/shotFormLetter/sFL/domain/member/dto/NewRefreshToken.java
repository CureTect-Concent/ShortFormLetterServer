package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class NewRefreshToken {
    private String newRefreshToken;
    private Date expiredTokenTime;
}
