package com.shotFormLetter.sFL.domain.post.controller;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.MusicListDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.PostInfoDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.ThumbnailDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
import com.shotFormLetter.sFL.domain.post.domain.service.PostService;

//import com.shotFormLetter.sFL.domain.post.s3.service.s3Service;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final s3UploadService s3UploadService;


    @PostMapping("/create")
    public MessageDto createPost (@RequestParam("title") String title,
                                  @RequestParam("content") String content,
                                  @RequestParam("imageList") List <MultipartFile> newImageList,
                                  @RequestParam("thumbnailList") List <MultipartFile> newthumbnailList,
                                  @RequestParam("openStatus") boolean openstatus,
                                  @RequestParam("media_reference") String media_reference,
                                  @RequestHeader("X-AUTH-TOKEN") String token){

            Member tokenMember=memberService.tokenMember(token);
            String userId = memberService.getUserIdFromMember(tokenMember);
            List<String> s3Urls=new ArrayList<>();
            String postId=postService.createPost(title,content,tokenMember,media_reference,userId,openstatus);
            postService.createLink(s3Urls,postId,userId,newImageList,newthumbnailList);
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("전송 완료");
            return messageDto;
        }
    @PutMapping("/modify")
    public ResponseEntity<?> modifyPost(@RequestParam("postId")Long postId,
                                        @RequestParam("content") String content,
                                        @RequestParam("title") String title,
                                        @RequestParam(value = "imageList",required = false) List <MultipartFile> newImageList,
                                        @RequestParam(value = "thumbnailList",required = false) List <MultipartFile> newthumbnailList,
                                        @RequestParam("openStatus") boolean openstatus,
                                        @RequestParam(value="new_media_reference",required = false) String new_media_reference,
                                        @RequestHeader("X-AUTH-TOKEN")String token){

        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        try {
            postService.updatePost(postId,content,title, new_media_reference, userId,openstatus, newImageList,newthumbnailList);
            MessageDto messageDto=new MessageDto();
            messageDto.setMessage("수정완료");
            return ResponseEntity.ok(messageDto);
        } catch (DataNotFoundException e) {
            MessageDto messageDto=new MessageDto();
            messageDto.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
    }

    @GetMapping("/find")
    public List<ThumbnailDto> getThList(@RequestHeader("X-AUTH-TOKEN") String token){
        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        List<ThumbnailDto> thList =postService.getThumbnailList(userId);
        return thList;
    }

    @GetMapping("/find/{id}")
    public PostInfoDto getInfo(@RequestHeader("X-AUTH-TOKEN") String token,
                               @PathVariable("id")Long postId){
        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        PostInfoDto postInfoDto=postService.getPostInfo(postId);
        return postInfoDto;
    }

    @GetMapping("/open/{id}")
    public ResponseEntity<?> openPost(@PathVariable("id") Long postId) {
        try {
            PostInfoDto postInfoDto = postService.openPostDto(postId);
            return ResponseEntity.ok(postInfoDto);
        } catch (DataNotFoundException e) {
            MessageDto messageDto=new MessageDto();
            messageDto.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
        }
    }

    @GetMapping("/urls")
    public MessageDto updatePost(@RequestParam("postId")Long postId, @RequestHeader("X-AUTH-TOKEN")String token){
        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        MessageDto messageDto=postService.modifyMessage(postId,userId);
        return messageDto;
    }

    @GetMapping("/music")
    public MusicListDto getList(@RequestHeader("X-AUTH-TOKEN")String token){
        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        return s3UploadService.getMusicList();
    }


    @DeleteMapping("/delete")
    public MessageDto deletePost(@RequestParam("userId")String userId,
                           @RequestParam("postId")Long postId,
                           @RequestHeader("X-AUTH-TOKEN")String token){
        Member tokenMember=memberService.tokenMember(token);
        if(tokenMember.getUsername().equals(userId)){
            return postService.deletePost(postId,userId);
        } else {
            return postService.getMessage();
        }
    }
}
