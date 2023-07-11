package com.shotFormLetter.sFL.domain.post.controller;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.post.domain.dto.*;

import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
import com.shotFormLetter.sFL.domain.post.domain.service.PostService;

import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MemberService memberService;


    @PostMapping("/create")
    public MessageDto createPost (@RequestParam(value = "title") String title,
                                  @RequestParam("content") String content,
                                  @RequestParam(value = "imageList",required = false) List <MultipartFile> newImageList,
                                  @RequestParam(value = "thumbnailList",required = false) List <MultipartFile> newthumbnailList,
                                  @RequestParam("openStatus") boolean openstatus,
                                  @RequestParam(value = "media_reference",required = false) String media_reference,
                                  @RequestParam(value = "musicId",required = false)Integer musicId,
                                  @RequestHeader("X-AUTH-TOKEN") String token){

            Member tokenMember=memberService.tokenMember(token);
            String userId = memberService.getUserIdFromMember(tokenMember);
            String id=tokenMember.getId().toString();
            List<String> s3Urls=new ArrayList<>();
            String postId=postService.createPost(title,content,tokenMember,media_reference,musicId,userId,openstatus);
            if (newImageList!=null && newthumbnailList!=null){
                postService.createLink(s3Urls,postId,userId,id,newImageList,newthumbnailList);
            }
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("전송 완료");
            return messageDto;
        }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyPost(@RequestParam(value = "postId")Long postId,
                                        @RequestParam(value = "content") String content,
                                        @RequestParam(value = "title") String title,
                                        @RequestParam(value = "imageList",required = false) List <MultipartFile> newImageList,
                                        @RequestParam(value = "thumbnailList",required = false) List <MultipartFile> newthumbnailList,
                                        @RequestParam(value="media_reference",required = false) String new_media_reference,
                                        @RequestParam(value = "openStatus") boolean openstatus,
                                        @RequestParam(value = "musicId",required = false)Integer musicId,
                                        @RequestHeader("X-AUTH-TOKEN")String token){


        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        postService.updatePost(postId,content,title, new_media_reference,musicId, userId,openstatus, newImageList,newthumbnailList);
        MessageDto messageDto=new MessageDto();
        messageDto.setMessage("수정완료");
        return ResponseEntity.ok(messageDto);
//        try {
//                postService.updatePost(postId,content,title, new_media_reference,musicId, userId,openstatus, newImageList,newthumbnailList);
//                MessageDto messageDto=new MessageDto();
//                messageDto.setMessage("수정완료");
//                return ResponseEntity.ok(messageDto);
//            } catch (DataNotFoundException e) {
//                MessageDto messageDto=new MessageDto();
//                messageDto.setMessage(e.getMessage());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDto);
//            }
    }

    @GetMapping("/find")
    public List<ThumbnailDto> getThList(@RequestHeader("X-AUTH-TOKEN") String token){
        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        List<ThumbnailDto> thList =postService.getThumbnailList(userId);
        return thList;
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getInfo(@RequestHeader("X-AUTH-TOKEN") String token,
                               @PathVariable("id")Long postId){

        Member tokenMember=memberService.tokenMember(token);
        String userId = memberService.getUserIdFromMember(tokenMember);
        PostInfoDto postInfoDto=postService.getPostInfo(postId,userId);
        return ResponseEntity.ok(postInfoDto);
    }

    @GetMapping("/open/{uuid}")
    public ResponseEntity<?> openPost(@PathVariable("uuid") String postId) {
        PostInfoDto postInfoDto = postService.openPostDto(postId);
        return ResponseEntity.ok(postInfoDto);
    }

    @PostMapping("/urls")
    public ResponseEntity<?> getUrl(@RequestHeader("X-AUTH-TOKEN")String token,
                             @RequestBody PostIdDto postId){
        Member tokenMember=memberService.tokenMember(token);
        Long getId=postId.getPostId();
        String userId = memberService.getUserIdFromMember(tokenMember);
        return ResponseEntity.ok(postService.urlMessage(getId,userId));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?>  deletePost(@RequestBody DeletePostDto deletePostDto, @RequestHeader("X-AUTH-TOKEN")String token){

        Long userId=deletePostDto.getUserSeq();
        Long postId=deletePostDto.getPostId();
        Member tokenMember=memberService.tokenMember(token);
        MessageDto messageDto=new MessageDto();
        postService.deletePost(postId, userId);
        messageDto.setMessage("삭제완료");
        return ResponseEntity.ok(messageDto);
    }
}
