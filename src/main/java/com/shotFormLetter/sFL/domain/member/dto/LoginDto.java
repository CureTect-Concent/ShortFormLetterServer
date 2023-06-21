package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class LoginDto {
    @NotNull(message = "아이디를 입력해주세요")
    private String userId;
    @NotNull(message = "비밀번호를 입력해주세요")
    private String password;
}