package com.shotFormLetter.sFL.domain.report.domain.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportList {
    private String reportId;
    private String title;
    private String user;
    private int reportCount;
}
