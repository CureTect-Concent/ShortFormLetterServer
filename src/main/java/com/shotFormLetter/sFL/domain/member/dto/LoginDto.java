package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class LoginDto {
    private String userId;
    private String password;
}