package com.shotFormLetter.sFL.domain.post.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostInfoDto {
    private Long postId;
    private String userId;
    private String title;
    private String content;
    private List<String> s3Urls;
    private List<String> media_reference;
    private LocalDateTime localDateTime;
    private Boolean openstatus;
}
