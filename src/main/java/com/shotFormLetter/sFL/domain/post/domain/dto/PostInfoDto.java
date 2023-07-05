package com.shotFormLetter.sFL.domain.post.domain.dto;

import com.shotFormLetter.sFL.domain.music.domain.dto.MusicInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostInfoDto {
    private String username;
    private String title;
    private String content;
    private List<MediaDto> mediaDto;
    private MusicInfo musicInfo;
    private LocalDateTime localDateTime;
    private String userProfile;
    private Integer view;
    private Boolean openstatus;
}
