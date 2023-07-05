package com.shotFormLetter.sFL.domain.member.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
@Getter
public class MemberDto {
    @NotNull(message = "아이디를 입력해주세요")
    private String userId;

    @NotNull(message = "회원 이름을 입력해주세요")
    private String userName;

    @NotNull(message = "비밀번호를 입력해주세요")
    private String password;
}