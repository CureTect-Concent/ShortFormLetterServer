package com.shotFormLetter.sFL.domain.post.domain.service;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.dto.PostInfoDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.ThumbnailDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    String createPost(String title, String content, Member tokenMember,List<String> media_reference,String userId,boolean openstauts);

    List<ThumbnailDto> getThumbnailList(String userId);

    PostInfoDto getPostInfo(Long postId);

    PostInfoDto openPostDto(Long postId);
    void createLink(List<String> s3Urls,String postId,String userId,List<MultipartFile> newimageList,List<MultipartFile> newthumbnailList);

}
