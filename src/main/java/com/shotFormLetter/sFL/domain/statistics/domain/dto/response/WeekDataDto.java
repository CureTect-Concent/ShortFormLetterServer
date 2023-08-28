package com.shotFormLetter.sFL.domain.statistics.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeekDataDto {
    private String year;
    private String month;
    private String start;
    private String end;
    private Integer count;
}
