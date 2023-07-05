package com.shotFormLetter.sFL.domain.post.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
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
import java.util.UUID;

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
        post.setPostUk(UUID.randomUUID().toString());
        post.setTitle(title);
        post.setContent(content);
        post.setMember(member);
        post.setUserId(userId);
        post.setMedia_reference(media_reference);
        post.setMusicInfo(musicId);
        post.setCreatedAt(LocalDateTime.now());
        post.setOpenStatus(openstatus);
        post.setView(0);
        post=postRepository.save(post);
        String postId = post.getPostId().toString();
        return postId;
    }

    @Override
    public void createLink(List<String> s3Urls,String postId,String userId,String id,List<MultipartFile> newimageList,List<MultipartFile> newthumbnailList){
        List<String> getusrls= s3UploadService.getUrls(newimageList,s3Urls,postId,id);
        s3UploadService.uploadThumbnail(newthumbnailList,userId,s3Urls,postId,id);
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
        String post_id=postId.toString();
        String user_pk=post.getMember().getId().toString();
        String now_reference=post.getMedia_reference();
        if (now_reference.equals("null") && new_media_reference.equals("null")) {
            now_reference="null";
        } else if(!now_reference.equals("null") && !new_media_reference.equals("null")){
            now_reference=getNewReference(now_reference,new_media_reference);
        } else if(!now_reference.equals("null")&& new_media_reference.equals("null") ){
            now_reference=now_reference;
        } else {
            now_reference=new_media_reference;
        }
        System.out.println("결과: "+now_reference);

        String id=post.getMember().getId().toString();
        List<String> geturls=post.getS3Urls();
        if(newImageList!=null && newthumbnailList!=null){
            geturls=s3UploadService.updategetUrls(newImageList,userId,geturls,postId.toString(),id);
            s3UploadService.updateThumbnail(newthumbnailList,post_id,user_pk);
        }

        post.setContent(content);
        post.setTitle(title);
        post.setOpenStatus(openstauts);
        post.setMedia_reference(now_reference);
        post.setMusicInfo(musicId);
        post.setS3Urls(geturls);
        post.setView(post.getView());
        postRepository.save(post);
        return post;
    }
    @Override
    public String getNewReference(String now_reference,String new_meida_reference){
        try {
            // geturls를 JSONArray로 변환
            JSONArray urls = new JSONArray(now_reference);
            JSONArray new_urls = new JSONArray(new_meida_reference);
            for (int i = 0; i < new_urls.length(); i++) {
                urls.put(new_urls.getJSONObject(i));
            }
                return urls.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return now_reference;
        }

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
        Integer view=post.getView();
        post.setView(view+1);
        Music music =musicRepository.getMusicByMusicNumber(post.getMusicInfo());
        MusicInfo musicInfo = music != null ? musicService.getUserMusicInfo(music.getMusicNumber()) : null;
        String media=post.getMedia_reference();
        List<String> s3urls=post.getS3Urls();
        List<MediaDto> mediaDtos=new ArrayList<>();
        if(s3urls.isEmpty()){
            mediaDtos=null;
        } else{
            mediaDtos= make(media,s3urls);
        }
        PostInfoDto postInfoDto =new PostInfoDto();
        postInfoDto.setUsername(post.getMember().getUserId());
        postInfoDto.setTitle(post.getTitle());
        postInfoDto.setContent(post.getContent());
        postInfoDto.setMediaDto(mediaDtos);
        postInfoDto.setMusicInfo(musicInfo);
        postInfoDto.setOpenstatus(post.getOpenStatus());
        postInfoDto.setUserProfile(post.getMember().getProfile());
        postInfoDto.setView(post.getView()+1);
        postInfoDto.setLocalDateTime(post.getCreatedAt());
        postRepository.save(post);
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
                System.out.println(url);
                jsonObject.put("s3url", url);
                System.out.println(jsonObject);

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
    public PostInfoDto openPostDto(String postId){
        Post post=postRepository.getPostByPostUk(postId);
        System.out.println("결과");
        System.out.println(post.getMember().getUserId());
        if(post==null){
            throw new DataNotFoundException("게시글 조회 안됨");
//            throw new IllegalStateException("게시글 조회 안됨");
        }
        if(post.getOpenStatus()==Boolean.FALSE){
            throw new DataNotFoundException("접근권한 없음");
        }
        Integer view=post.getView();
        post.setView(view+1);
        Music music =musicRepository.getMusicByMusicNumber(post.getMusicInfo());
        MusicInfo musicInfo = music != null ? musicService.getUserMusicInfo(music.getMusicNumber()) : null;
        String media=post.getMedia_reference();
        List<String> s3urls=post.getS3Urls();
        List<MediaDto> mediaDtos=new ArrayList<>();
        if(s3urls.isEmpty()){
            mediaDtos=null;
        } else{
            mediaDtos= make(media,s3urls);
        }
        PostInfoDto postInfoDto =new PostInfoDto();
        postInfoDto.setUsername(post.getMember().getUserId());
        postInfoDto.setTitle(post.getTitle());
        postInfoDto.setContent(post.getContent());
        postInfoDto.setMediaDto(mediaDtos);
        postInfoDto.setMusicInfo(musicInfo);
        postInfoDto.setUserProfile(post.getMember().getProfile());
        postInfoDto.setView(post.getView()+1);
        postInfoDto.setOpenstatus(post.getOpenStatus());
        postInfoDto.setLocalDateTime(post.getCreatedAt());
        postRepository.save(post);
        return postInfoDto;
    }

    @Override
    public void deletePost(Long getPostId,Long userSeq) {
        Post getPost = postRepository.getPostByPostId(getPostId);
        if (getPost == null) {
            throw new DataNotFoundException("게시글이 없습니다.");
        } else if (!getPost.getMember().getId().equals(userSeq)) {
            throw new DataNotFoundException("삭제할 권한이 없습니다.");
        }
        if (getPost.getMember().getId().equals(userSeq)) {
            List<String> s3urls = getPost.getS3Urls();
            String postId = getPost.getPostId().toString();
            s3UploadService.deleteList(s3urls);
            postRepository.delete(getPost);
        }
    }
//        MessageDto messageDto= new MessageDto();
//        Post post= postRepository.getPostByPostId(getPostId);
//        if(post==null){
//            throw new DataNotFoundException("게시물 조회 안됨");
//        } else {
//            List<String> s3urls=post.getS3Urls();
//            String postId=post.getPostId().toString();
//            s3UploadService.deleteList(s3urls);
//            postRepository.delete(post);
//            messageDto.setMessage("삭제완료");
//        }
//        return messageDto;

    @Override
    public MessageDto getMessage(){
        MessageDto messageDto=new MessageDto();
        messageDto.setMessage("삭제 권한 없음");
        return messageDto;
    }

    @Override
    public MessageDto urlMessage(Long getPostId,String userId){
        Post post=postRepository.getPostByPostId(getPostId);
        System.out.println("결과:"+post);
        MessageDto messageDto=new MessageDto();
        if (post == null) {
            throw new DataNotFoundException("게시글이 없습니다.");
        } else if(post.getOpenStatus()==Boolean.FALSE){
            throw new DataNotFoundException("공개수정이 필요합니다.");
        } else{
            String urlId = post.getPostId().toString();
            messageDto.setMessage("https://shared.concents.io/letter/"+post.getPostUk());
        }
        return messageDto;
    }
}
