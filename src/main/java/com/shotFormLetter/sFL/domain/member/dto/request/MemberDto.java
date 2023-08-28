package com.shotFormLetter.sFL.domain.member.dto.request;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.dto.validation.EmojiDto;
import lombok.Getter;

@Getter
public class MemberDto {
    private String userId;
    private String userName;
    private String password;

    public void validate() {
        EmojiDto emojidto=new EmojiDto();
        if (userId != null) {
            if(emojidto.findEmoji(userId)==Boolean.TRUE){
                throw new DataNotFoundException("회원 ID에 이모티콘은 불가능합니다.");
            } else if(userId.length()<5 || userId.length()>30) {
            throw new DataNotFoundException("회원 ID는 5~30자 사이만 가능합니다");
            }
        } else if(userId==null || userId.equals("null")){
            throw new DataNotFoundException("회원 ID는 필수입니다");
        }

        if(userName !=null){
            if(emojidto.findEmoji(userName)==Boolean.TRUE){
                throw new DataNotFoundException("회원 이름에 이모티콘은 불가능합니다");
            } else if(userName.length()<2 || userName.length()>30){
                throw new DataNotFoundException("회원이름은 2~30자 사이만 가능합니다");
            }
        } else if (userName==null || userName.equals("null")) {
            throw new DataNotFoundException("회원이름은 필수입니다");
        }

        if (password != null) {
            if(emojidto.findEmoji(password)==Boolean.TRUE){
                throw new DataNotFoundException("비밀번호에 이모티콘은 불가능합니다");
            } else if(password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$")==Boolean.FALSE) {
                throw new DataNotFoundException("비밀번호는 영어와 숫자를 포함한 6글자 이상이어야합니다.");
            }
        }else if(password==null || password.equals("null")){
            throw new DataNotFoundException("비밀번호는 필수입니다");
        }
    }
}