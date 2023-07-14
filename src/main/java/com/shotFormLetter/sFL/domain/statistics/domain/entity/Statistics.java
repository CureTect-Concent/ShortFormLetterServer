//package com.shotFormLetter.sFL.domain.statistics.domain.entity;
//
//import com.shotFormLetter.sFL.domain.member.entity.Member;
//import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
//import lombok.*;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Statistics {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long StatisticsId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @CollectionTable(name = "statistics_post", joinColumns = @JoinColumn(name = "member_id"))
//    private List<Post> post= new ArrayList<>();
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @CollectionTable(name="statistics_post_view", joinColumns = @JoinColumn(name="member_id"))
//    private List<Integer> vewlist=new ArrayList<>();
//}
