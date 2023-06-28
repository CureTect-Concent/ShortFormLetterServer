package com.shotFormLetter.sFL.domain.post.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.music.domain.dto.MusicInfo;
import com.shotFormLetter.sFL.domain.music.domain.entity.Music;
import com.shotFormLetter.sFL.domain.music.domain.repository.MusicRepository;
import com.shotFormLetter.sFL.domain.music.domain.service.MusicService;
import com.shotFormLetter.sFL.domain.post.domain.dto.MediaDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.PostInfoDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.ThumbnailDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
//import com.shotFormLetter.sFL.domain.post.s3.service.s3Service;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final s3UploadService s3UploadService;
    private final MusicRepository musicRepository;
    private final MusicService musicService;


    @Override
    public String createPost(String title, String content, Member member, String media_reference,Integer musicId,String userId,boolean openstatus){
        Post post= new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setMember(member);
        post.setUserId(userId);
        post.setMedia_reference(media_reference);
        post.setMusicInfo(musicId);
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
    public Post updatePost(Long postId,String content,String title, String new_media_reference,Integer musicId,
                           String userId,boolean openstauts, List <MultipartFile> newImageList, List <MultipartFile> newthumbnailList){
        Post post =postRepository.getPostByPostId(postId);
        if(post==null){
            throw new IllegalStateException("게시글 조회 안됨");
        }
        List<String> geturls=post.getS3Urls();
        geturls=s3UploadService.updategetUrls(newImageList,userId,geturls,postId.toString());
        s3UploadService.uploadThumbnail(newthumbnailList,userId,geturls,postId.toString());

        String new_reference=post.getMedia_reference();
        new_reference=getNewReference(new_reference,new_media_reference);
        post.setContent(content);
        post.setTitle(title);
        post.setOpenStatus(openstauts);
        post.setMedia_reference(new_reference);
        post.setMusicInfo(musicId);
        post.setS3Urls(geturls);
        postRepository.save(post);
        return post;
    }
    @Override
    public String getNewReference(String new_reference,String new_meida_reference){
        try {
            // geturls를 JSONArray로 변환
            JSONArray urls = new JSONArray(new_reference);
            JSONArray new_urls = new JSONArray(new_meida_reference);
            for (int i = 0; i < new_urls.length(); i++) {
                urls.put(new_urls.getJSONObject(i));
            }
            return urls.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new_reference;
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
        Music music =musicRepository.getMusicByMusicNumber(post.getMusicInfo());
        MusicInfo musicInfo=musicService.getUserMusicInfo(music.getMusicNumber());
        if(post==null){
            throw new IllegalStateException("게시글 조회 안됨");
        }
        String media=post.getMedia_reference();
        List<String> s3urls=post.getS3Urls();
        List<MediaDto> mediaDtos= make(media,s3urls);
        PostInfoDto postInfoDto =new PostInfoDto();
        postInfoDto.setPostId(post.getPostId());
        postInfoDto.setUsername(post.getMember().getUserId());
        postInfoDto.setTitle(post.getTitle());
        postInfoDto.setContent(post.getContent());
        postInfoDto.setMediaDto(mediaDtos);
        postInfoDto.setMusicInfo(musicInfo);
        postInfoDto.setOpenstatus(post.getOpenStatus());
        postInfoDto.setLocalDateTime(post.getCreatedAt());;
        return postInfoDto;
    }

    @Override
    public List<MediaDto> make(String media, List<String> s3urls){
        List<MediaDto> MediaDtos=new ArrayList<>();
        JSONArray jsonArray = new JSONArray(media);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = s3urls.get(i);
                jsonObject.put("s3url", url);

                MediaDto mediaDto = objectMapper.readValue(jsonObject.toString(), MediaDto.class);
                MediaDtos.add(mediaDto);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MediaDtos;
    }

    @Override
    public PostInfoDto openPostDto(Long postId){
        Post post=postRepository.getPostByPostId(postId);
        Music music =musicRepository.getMusicByMusicNumber(post.getMusicInfo());
        MusicInfo musicInfo=musicService.getUserMusicInfo(music.getMusicNumber());
        if(post==null){
            throw new DataNotFoundException("게시글 조회 안됨");
//            throw new IllegalStateException("게시글 조회 안됨");
        }
        if(post.getOpenStatus()==Boolean.FALSE){
            throw new DataNotFoundException("접근권한 없음");
        }
        String media=post.getMedia_reference();
        List<String> s3urls=post.getS3Urls();
        List<MediaDto> mediaDtos= make(media,s3urls);
        PostInfoDto postInfoDto =new PostInfoDto();
        postInfoDto.setPostId(post.getPostId());
        postInfoDto.setUsername(post.getMember().getUserId());
        postInfoDto.setTitle(post.getTitle());
        postInfoDto.setContent(post.getContent());
        postInfoDto.setMediaDto(mediaDtos);
        postInfoDto.setMusicInfo(musicInfo);
        postInfoDto.setOpenstatus(post.getOpenStatus());
        postInfoDto.setLocalDateTime(post.getCreatedAt());;
        return postInfoDto;
    }

    @Override
    public MessageDto deletePost(Long getPostId,String userId){
        MessageDto messageDto= new MessageDto();
        Post post= postRepository.getPostByPostId(getPostId);
        if(post==null){
            messageDto.setMessage("게시물 조회 안됨");
        } else {
            List<String> s3urls=post.getS3Urls();
            String postId=post.getPostId().toString();
            s3UploadService.deleteList(s3urls);
            postRepository.delete(post);
            messageDto.setMessage("삭제완료");
        }
        return messageDto;
    }
    @Override
    public MessageDto getMessage(){
        MessageDto messageDto=new MessageDto();
        messageDto.setMessage("삭제 권한 없음");
        return messageDto;
    }

    @Override
    public MessageDto modifyMessage(Long getPostId,String userId){
        Post post=postRepository.getPostByPostId(getPostId);
        System.out.println("결과:"+post);
        MessageDto messageDto=new MessageDto();
        if (post == null) {
            messageDto.setMessage("게시물 조회 없음");
        } else if(post.getOpenStatus()==Boolean.FALSE){
            messageDto.setMessage("공개설정 수정 필요함");
        } else{
            String urlId = post.getPostId().toString();
            messageDto.setMessage("http://192.168.1.50:3001/letter/"+urlId);
        }
        return messageDto;
    }
}
