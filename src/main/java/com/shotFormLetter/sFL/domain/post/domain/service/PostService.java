package com.shotFormLetter.sFL.domain.post.domain.service;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.dto.*;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    String createPost(String title, String content, Member tokenMember,String media_reference,Integer musicId,String userId,boolean openstauts);

    List<ThumbnailDto> getThumbnailList(String userId);


    Post updatePost(Long postId,String content,String title, String new_media_reference,String delete_index,Integer musicId,String userId,boolean openstauts,
                    List <MultipartFile> newImageList, List <MultipartFile> newthumbnailList);
    PostInfoDto getPostInfo(Long postId,String userId);

    String getReference(String now_reference,String new_media_reference);
//    String getReference(String now_reference, String list);

    List<Long> converToList(JSONArray jsonArray);
    MediaAndUrlsDto getNewReference(String now_reference, String delete_index,List<String> geturls);
    PostInfoDto openPostDto(String postId);
    void createLink(List<String> s3Urls,String postId,String userId,String id,List<MultipartFile> newimageList,List<MultipartFile> newthumbnailList);
//    void createAction(List<String> s3Urls,String postId,String userId,List<MultipartFile> newimageList);
    void deletePost(Long getPostId,Long userSeq);

//    List<String> deletePostImage(List<String> s3Urls, String list);
//    public List<String> makeMediaInfo(String media, List<String> s3urls);

    public List<MediaDto> make(String media,List<String> s3urls);
    MessageDto getMessage();

    MessageDto urlMessage(Long getPostId,String userId);
}
