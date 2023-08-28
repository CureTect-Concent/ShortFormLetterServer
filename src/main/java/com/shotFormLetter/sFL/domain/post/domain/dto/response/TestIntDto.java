package com.shotFormLetter.sFL.domain.post.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TestIntDto {
    private StopWatch stopWatch;
    private  List<Integer> test;

    private StopWatch stopWatch2;
    private List<Integer> test2;
}
