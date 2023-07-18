//package com.shotFormLetter.sFL.domain.statistics.domain.service;
//
//
//import com.shotFormLetter.sFL.domain.member.entity.Member;
//import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
//import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
//import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
//import com.shotFormLetter.sFL.domain.statistics.domain.entity.Statistics;
//import com.shotFormLetter.sFL.domain.statistics.domain.repository.StatisticsRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//
//@RequiredArgsConstructor
//@Service
//public class StatisticsService {
//
//    private final StatisticsRepository statisticsRepository;
//    private final MemberRepository memberRepository;
//    private final PostRepository postRepository;
//    public void CreateStatistics(Long memberId, Post post){
//
//        Optional<Member> joinMember=memberRepository.findById(memberId);
//        Statistics memberStatistics = statisticsRepository.getStatisticsByMember(joinMember.get());
//
//
////        Statistics memberStatistics = Statistics.builder()
////                .member(joinMember.get())
////                .post(null)
////                .vewlist(null)
////                .build();
//        statisticsRepository.save(memberStatistics).getStatisticsId();
//    }
//}
