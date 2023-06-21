package com.shotFormLetter.sFL.domain.post.domain.entity;


import com.shotFormLetter.sFL.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name="title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 매핑할 외래키 이름
    private Member member;

    @ElementCollection
    @Column(name="s3urls")
    @CollectionTable(name = "post_s3_urls", joinColumns = @JoinColumn(name = "post_id"))
    private List<String> s3Urls;

    @Column(name="media_reference")
    @ElementCollection
    private List<String> media_reference;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name="author")
    private String userId;

    @Column(name="open_status")
    private Boolean openStatus;
}
