package com.shotFormLetter.sFL.domain.member.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String userName;
    private Long userSeq;
    private String userProfile;
    private Boolean adsStatus;
}
