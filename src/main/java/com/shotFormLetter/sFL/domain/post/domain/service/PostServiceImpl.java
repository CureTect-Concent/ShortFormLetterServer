package com.shotFormLetter.sFL.domain.post.domain.service;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.dto.PostInfoDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.ThumbnailDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
//import com.shotFormLetter.sFL.domain.post.s3.service.s3Service;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
//    private final s3Service s3Service;
    private final s3UploadService s3UploadService;


    @Override
    public String createPost(String title, String content, Member member, List<String> media_reference,String userId,boolean openstatus){
        Post post= new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setMember(member);
        post.setUserId(userId);
        post.setMedia_reference(media_reference);
        post.setCreatedAt(LocalDateTime.now());
        post.setOpenStatus(openstatus);
        post=postRepository.save(post);
        String postId = post.getPostId().toString();
        return postId;
    }

    @Override
    public void createLink(List<String> s3Urls,String postId,String userId,List<MultipartFile> newimageList,List<MultipartFile> newthumbnailList){
        List<String> getusrls= s3UploadService.getUrls(newimageList,userId,s3Urls,postId.toString());
        s3UploadService.uploadThumbnail(newthumbnailList,userId,s3Urls,postId.toString());
        Post post=postRepository.getPostByPostId(Long.parseLong(postId));
        post.setS3Urls(getusrls);
        post=postRepository.save(post);
    }

    @Override
    public List<ThumbnailDto> getThumbnailList(String userId){
        List<Post> posts=postRepository.getPostByUserId(userId);
        List<ThumbnailDto> thumbnailDto =new ArrayList<>();
        for(Post post:posts){
            ThumbnailDto thmp=new ThumbnailDto();
            thmp.setPostId(post.getPostId());
            thmp.setTitle(post.getTitle());
            thmp.setUserName(userId);
            thmp.setLocalDateTime(post.getCreatedAt());
            thumbnailDto.add(thmp);
        }
        return thumbnailDto;
    }

    @Override
    public PostInfoDto getPostInfo(Long postId){
        Post post=postRepository.getPostByPostId(postId);
        if(post==null){
            throw new IllegalStateException("게시글 조회 안됨");
        }
        PostInfoDto postInfoDto =new PostInfoDto();
        postInfoDto.setPostId(post.getPostId());
        postInfoDto.setUserId(post.getUserId());
        postInfoDto.setTitle(post.getTitle());
        postInfoDto.setContent(post.getContent());
        postInfoDto.setS3Urls(post.getS3Urls());
        postInfoDto.setMedia_reference(post.getMedia_reference());
        postInfoDto.setOpenstatus(post.getOpenStatus());
        postInfoDto.setLocalDateTime(post.getCreatedAt());
        return postInfoDto;
    }
    @Override
    public PostInfoDto openPostDto(Long postId){
        Post post=postRepository.getPostByPostId(postId);
        if(post.getOpenStatus()==Boolean.FALSE){
            throw new IllegalStateException("접근권한 없음");
        }
        if(post==null){
            throw new IllegalStateException("게시글 조회 안됨");
        }
        PostInfoDto postInfoDto =new PostInfoDto();
        postInfoDto.setPostId(post.getPostId());
        postInfoDto.setUserId(post.getUserId());
        postInfoDto.setTitle(post.getTitle());
        postInfoDto.setContent(post.getContent());
        postInfoDto.setS3Urls(post.getS3Urls());
        postInfoDto.setMedia_reference(post.getMedia_reference());
        postInfoDto.setOpenstatus(post.getOpenStatus());
        postInfoDto.setLocalDateTime(post.getCreatedAt());
        return postInfoDto;
    }
}
