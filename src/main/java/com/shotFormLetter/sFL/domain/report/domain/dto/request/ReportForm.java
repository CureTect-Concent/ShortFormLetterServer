package com.shotFormLetter.sFL.domain.report.domain.dto.request;


import com.shotFormLetter.sFL.domain.report.domain.entity.CateGory;
import lombok.Getter;

@Getter
public class ReportForm {
    private Long postId;
    private String message;
    private String cateGory;
}
