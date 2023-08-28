package com.shotFormLetter.sFL.domain.statistics.domain.dto.response;


import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
public class StatisticsDto {
    private List<Most5PostDto> most5PostDtos;
    private Integer total;
}
