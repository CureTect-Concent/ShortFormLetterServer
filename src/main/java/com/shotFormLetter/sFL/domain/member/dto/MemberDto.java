package com.shotFormLetter.sFL.domain.member.dto;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import lombok.Getter;

import javax.validation.constraints.NotNull;
@Getter
public class MemberDto {
    private String userId;
    private String userName;
    private String password;

    public void validate() {
        if (userId != null) {
            if(userId.matches(".*[\\p{So}].*")==Boolean.TRUE){
                throw new DataNotFoundException("회원 ID에 이모티콘은 불가능합니다.");
            } else if(userId.length()<5) {
            throw new DataNotFoundException("회원 ID는 5자 이상이어야 합니다");
            }
        } else if(userId==null || userId.equals("null")){
            throw new DataNotFoundException("회원 ID는 필수입니다");
        }

        if (userName == null || userName.equals("null")) {
            throw new DataNotFoundException("회원 이름을 입력해주세요");
        } else if(userName.length()<2){
            throw new DataNotFoundException("회원 이름은 2자 이상이어야 합니다");
        }

        if (password != null) {
            if(password.matches(".*[\\p{So}].*")==Boolean.TRUE){
                throw new DataNotFoundException("비밀번호에 이모티콘은 불가능합니다");
            } else if(password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$")==Boolean.FALSE) {
                throw new DataNotFoundException("비밀번호는 영어와 숫자를 포함한 6글자 이상이어야합니다.");
            }
        }else if(password==null || password.equals("null")){
            throw new DataNotFoundException("비밀번호는 필수입니다");
        }
    }
}