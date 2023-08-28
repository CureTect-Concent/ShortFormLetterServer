package com.shotFormLetter.sFL.domain.statistics.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TimeLineDto {
    private String start;
    private String end;
    private String type;
    private String notice;
}
