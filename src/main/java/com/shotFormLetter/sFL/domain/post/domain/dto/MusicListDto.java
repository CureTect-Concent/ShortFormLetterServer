package com.shotFormLetter.sFL.domain.post.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MusicListDto {
    private List<String> urls =new ArrayList<>();
}
