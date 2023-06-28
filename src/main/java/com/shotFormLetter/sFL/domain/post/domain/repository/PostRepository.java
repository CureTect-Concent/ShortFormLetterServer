package com.shotFormLetter.sFL.domain.post.domain.repository;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> getPostByUserId(String userId);
    Post getPostByPostId(Long postId);

}
