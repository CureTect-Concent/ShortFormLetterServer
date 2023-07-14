//package com.shotFormLetter.sFL.domain.statistics.domain.service;
//
//
//import com.shotFormLetter.sFL.domain.member.entity.Member;
//import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
//import com.shotFormLetter.sFL.domain.statistics.domain.entity.Statistics;
//import com.shotFormLetter.sFL.domain.statistics.domain.repository.StatisticsRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//
//@RequiredArgsConstructor
//@Service
//public class StatisticsService {
//
//    private final StatisticsRepository statisticsRepository;
//    private final MemberRepository memberRepository;
//
//
//    public void BasicCreateStatistics(Long memberId){
//
//        Optional<Member> joinMember=memberRepository.findById(memberId);
//        Statistics memberStatistics = Statistics.builder()
//                .member(joinMember.get())
//                .post(null)
//                .vewlist(null)
//                .build();
//        statisticsRepository.save(memberStatistics).getStatisticsId();
//    }
//}
