package com.shotFormLetter.sFL.domain.post.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ThumbnailDto {
    private Long postId;
    private String title;
    private LocalDateTime localDateTime;
    private String userName;
}
