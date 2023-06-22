package com.shotFormLetter.sFL.domain.post.controller;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.post.domain.dto.MessageDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.PostInfoDto;
import com.shotFormLetter.sFL.domain.post.domain.dto.ThumbnailDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
import com.shotFormLetter.sFL.domain.post.domain.service.PostService;

//import com.shotFormLetter.sFL.domain.post.s3.service.s3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//    private final s3Service s3Service;


    @PostMapping("/create")
    public MessageDto createPost (@RequestParam("title") String title,
                                  @RequestParam("content") String content,
                                  @RequestParam("imageList") List <MultipartFile> newImageList,
                                  @RequestParam("thumbnailList") List <MultipartFile> newthumbnailList,
                                  @RequestParam("openStatus") boolean openstatus,
                                  @RequestParam("media_reference") List <String> media_reference,
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
    public PostInfoDto openPost(@PathVariable("id")Long postId){
        PostInfoDto postInfoDto=postService.openPostDto(postId);
        return postInfoDto;
    }
//    @PutMapping("/update/{postId}")
//    public void updatePost(@PathVariable("postId")Long postId,
//                           @RequestHeader("X-AUTH-TOKEN")String token,
//                           @RequestParam())
}
