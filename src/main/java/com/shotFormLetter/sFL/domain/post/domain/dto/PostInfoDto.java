package com.shotFormLetter.sFL.domain.post.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostInfoDto {
    private Long postId;
    private String username;
    private String title;
    private String content;
    private List<MediaDto> mediaDto;
    private LocalDateTime localDateTime;
    private Boolean openstatus;
}
