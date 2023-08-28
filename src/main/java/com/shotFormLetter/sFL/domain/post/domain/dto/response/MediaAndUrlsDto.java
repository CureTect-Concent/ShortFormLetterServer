package com.shotFormLetter.sFL.domain.post.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaAndUrlsDto {
    private String now_reference;
    private List<String> s3urls;
}
