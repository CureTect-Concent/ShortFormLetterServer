package com.shotFormLetter.sFL.domain.statistics.domain.dto.request;


import lombok.Getter;

@Getter
public class UserCustomDto {
    private Long userSeq;
    private String start;
    private String end;
    private String type;
}
