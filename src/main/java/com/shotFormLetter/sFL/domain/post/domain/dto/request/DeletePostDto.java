package com.shotFormLetter.sFL.domain.post.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeletePostDto {
    private Long postId;
    private Long userSeq;
}
