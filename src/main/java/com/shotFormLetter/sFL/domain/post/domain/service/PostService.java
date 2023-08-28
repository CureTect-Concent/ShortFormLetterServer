package com.shotFormLetter.sFL.domain.post.domain.service;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.dto.response.*;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    void createPost(String title, String content, Member member, String media_reference,Integer musicId,String userId,boolean openstatus,
               List<MultipartFile> newimageList,List<MultipartFile> newthumbnailList);

    List<ThumbnailDto> getThumbnailList(String userId);

    Post updatePost(Long postId,String content,String title, String new_media_reference,String delete_index,Integer musicId,String userId,boolean openstauts,
                    List <MultipartFile> newImageList, List <MultipartFile> newthumbnailList);
    PostInfoDto getPostInfo(Long postId, String userId);

    String getReference(String now_reference,String new_media_reference);

    List<Long> converToList(JSONArray jsonArray);
    MediaAndUrlsDto getNewReference(String now_reference, String delete_index, List<String> geturls);
    PostInfoDto openPostDto(String postId);
//    void createLink(List<String> s3Urls,String postId,String userId,String id,List<MultipartFile> newimageList,List<MultipartFile> newthumbnailList);

    void deletePost(Long getPostId,Long userSeq);

    public List<MediaDto> make(String media, List<String> s3urls);

    MessageDto urlMessage(Long getPostId, String userId);
}
