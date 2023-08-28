package com.shotFormLetter.sFL.domain.statistics.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Most5PostDto {
    private String title;
    private Integer views;
}
