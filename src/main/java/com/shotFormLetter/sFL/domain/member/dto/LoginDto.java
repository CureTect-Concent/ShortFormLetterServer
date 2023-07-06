package com.shotFormLetter.sFL.domain.member.dto;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class LoginDto {
    private String userId;
    private String password;

    public void validate() {
        if (userId == null || userId.equals("null")) {
            throw new DataNotFoundException("아이디를 입력해주세요");
        }
        if (password == null || password.equals("null")) {
            throw new DataNotFoundException("비밀번호를 입력해주세요");
        }
    }
}