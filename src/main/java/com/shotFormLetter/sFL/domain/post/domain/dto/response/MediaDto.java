package com.shotFormLetter.sFL.domain.post.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaDto {
    private String reference;
    private String s3url;
    private String type;
}
