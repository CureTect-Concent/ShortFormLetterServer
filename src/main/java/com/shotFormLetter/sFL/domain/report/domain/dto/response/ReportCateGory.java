package com.shotFormLetter.sFL.domain.report.domain.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportCateGory {
    private List<String> categoryValues;
}
