package com.shotFormLetter.sFL.domain.statistics.domain.service;


import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
import com.shotFormLetter.sFL.domain.statistics.domain.dto.response.*;
import com.shotFormLetter.sFL.domain.statistics.domain.entity.Statistics;
import com.shotFormLetter.sFL.domain.statistics.domain.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


@RequiredArgsConstructor
@Service
@Slf4j
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;



    public void addStatisticsPost(Post post,LocalDateTime localDateTime,Member member){

        List<String> viewTime=new ArrayList<>();
        Statistics statistics=Statistics.builder()
                    .postTimeLineId(post.getPostId())
                    .memberId(member.getId())
                    .viewTime(viewTime)
                    .view(0)
                    .build();
        statisticsRepository.save(statistics);
    }

    public void addTimeLine(Post post,LocalDateTime localDateTime){
        Statistics statistics=statisticsRepository.getByPostTimeLineId(post.getPostId());
        List<String> timeLine=statistics.getViewTime();
        timeLine.add(localDateTime.toString());
        Integer view=statistics.getView()+1;
        statistics.setViewTime(timeLine);
        statistics.setView(view);
        statisticsRepository.save(statistics);
    }

    public void deleteUserAllTimeLine(Long memberId){
        List<Statistics> statisticsList=statisticsRepository.getListByMemberId(memberId);
        for(Statistics s : statisticsList){
            statisticsRepository.delete(s);
        }
    }

    public List<OneDayDataDto> getOneDayTimeLine(Long userSeq){
        List<Statistics> statistics=statisticsRepository.getListByMemberId(userSeq);
        if (statistics == null) {
            throw new DataNotFoundException("아직 통계가 잡히지 않았습니다");
        }

        List<String> getTimeLine= new ArrayList<>();
        for(Statistics s : statistics){
            List<String> timeLane = s.getViewTime();
            for(String t : timeLane){
                LocalDateTime dateTime=LocalDateTime.parse(t);
                getTimeLine.add(dateTime.toLocalDate().toString());
            }
        }

        Integer during=0;

        // 함수 호출시 받는 시간
        LocalDate timeLane= LocalDate.now();

        LocalDate startTime=timeLane;

        if(timeLane.getDayOfMonth()==1){
            startTime=timeLane.minusMonths(1);
            during=timeLane.minusDays(1).getDayOfMonth();
        } else {
            startTime=timeLane.minusDays(timeLane.getDayOfMonth()-1);
            during=timeLane.getDayOfMonth()-1;
        }


        List<OneDayDataDto> getUserTimeLine = new ArrayList<>();


        for(int i=0; i<during; i++){
            OneDayDataDto oneDayDataDto =new OneDayDataDto();

            LocalDate userTimeLine=startTime.plusDays(i);
            oneDayDataDto.setDate(userTimeLine.toString());

            Integer cnt = Collections.frequency(getTimeLine,userTimeLine.toString());
            oneDayDataDto.setCount(cnt);

            getUserTimeLine.add(oneDayDataDto);
        }
        return getUserTimeLine;
    }

    public List<MonthDataDto> getMonthTimeLine(Long userSeq){
        List<Statistics> statistics = statisticsRepository.getListByMemberId(userSeq);
        if (statistics == null) {
            throw new DataNotFoundException("아직 통계가 잡히지 않았습니다");
        }
        LocalDate timeLaneOfMonth=LocalDate.now();

        LocalDate startMonth=timeLaneOfMonth;

        Integer during=0;


        if(timeLaneOfMonth.getMonthValue()==1){
            startMonth=timeLaneOfMonth.minusYears(1);
            during=12;
        } else {
            startMonth=timeLaneOfMonth.minusMonths(timeLaneOfMonth.getMonthValue()-1);
            during=timeLaneOfMonth.getMonthValue()-1;
        }


        List<String> getMonthTimeLane = new ArrayList<>();

        for(Statistics s: statistics){
            List<String> monthLane=s.getViewTime();
            for(String mt:monthLane){
                LocalDateTime dateTime=LocalDateTime.parse(mt);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                String month=dateTime.format(formatter);
                getMonthTimeLane.add(month);
            }
        }
        System.out.println(getMonthTimeLane);

        List<MonthDataDto> monthDataDtos=new ArrayList<>();

        for(int i=0; i<during; i++) {
            MonthDataDto monthDataDto=new MonthDataDto();
            LocalDate getUserMonthTimeLane=startMonth.plusMonths(i);
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM");
            String month= getUserMonthTimeLane.format(formatter);
            Integer cnt = Collections.frequency(getMonthTimeLane,month);
            monthDataDto.setMonth(month);
            monthDataDto.setCount(cnt);
            monthDataDtos.add(monthDataDto);
        }
        return monthDataDtos;
    }


    public List<WeekDataDto> getWeekTimeLine(Long userSeq){
        List<Statistics> statistics = statisticsRepository.getListByMemberId(userSeq);
        if (statistics == null) {
            throw new DataNotFoundException("아직 통계가 잡히지 않았습니다");
        }

        LocalDate weekTimeLane=LocalDate.now();

        LocalDate firstDayOfMonth=weekTimeLane;
        LocalDate check=firstDayOfMonth;
        LocalDate reference=weekTimeLane;

        if(weekTimeLane.getDayOfMonth()==1){
            firstDayOfMonth=weekTimeLane.minusMonths(1);
            check=firstDayOfMonth;
            reference=weekTimeLane.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        } else {
            firstDayOfMonth=weekTimeLane.minusDays(weekTimeLane.getDayOfMonth()-1);
            check = firstDayOfMonth;
        }
        List<String> getWeekTimeLane = new ArrayList<>();

        for(Statistics s : statistics){
            List<String> timeLane = s.getViewTime();
            for(String t : timeLane){
                LocalDateTime dateTime=LocalDateTime.parse(t);
                getWeekTimeLane.add(dateTime.toLocalDate().toString());
            }
        }

        List<WeekDataDto> weekDataDtos = new ArrayList<>();

        while(check.isBefore(reference) || check==reference){
            DayOfWeek dayOfWeek=check.getDayOfWeek();

            if(dayOfWeek.equals(DayOfWeek.MONDAY)){
                WeekDataDto weekDataDto = new WeekDataDto();
                LocalDate last=check.plusDays(6);
                Integer count=0;
                Integer year=weekTimeLane.getYear();
                Integer month=weekTimeLane.getMonthValue();
                weekDataDto.setYear(year.toString());
                weekDataDto.setMonth(month.toString());
                weekDataDto.setStart(check.toString());
                weekDataDto.setEnd(last.toString());

                for(String weekTime:getWeekTimeLane){
                    LocalDate wt=LocalDate.parse(weekTime);
                    if((wt.isEqual(check) || wt.isAfter(check)) && (wt.isEqual(last) || wt.isBefore(last))){
                        count = count +1;
                    }
                }
                weekDataDto.setCount(count);
                weekDataDtos.add(weekDataDto);
            } else {
                WeekDataDto weekDataDto = new WeekDataDto();
                check=firstDayOfMonth.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                LocalDate last=check.plusDays(6);
                Integer count=0;
                Integer year=weekTimeLane.getYear();
                Integer month=weekTimeLane.getMonthValue();
                weekDataDto.setYear(year.toString());
                weekDataDto.setMonth(month.toString());
                weekDataDto.setStart(check.toString());
                weekDataDto.setEnd(last.toString());
                for(String weekTime:getWeekTimeLane){
                    LocalDate wt=LocalDate.parse(weekTime);
                    if((wt.isEqual(check) || wt.isAfter(check)) && (wt.isEqual(last) || wt.isBefore(last))){
                        count = count +1;
                    }
                }
                weekDataDto.setCount(count);
                weekDataDtos.add(weekDataDto);
            }
            check=firstDayOfMonth.plusDays(6);
            firstDayOfMonth=firstDayOfMonth.plusDays(6);
        }
        return weekDataDtos;
    }


    public StatisticsDto getMostposts(Long userSeq){
        Optional<Member> optionalMember=memberRepository.findById(userSeq);
        List<Post> post = postRepository.getPostByUserId(optionalMember.get().getUserId());
        if(post.size() < 1 ){
            throw new DataNotFoundException("통계 데이터가 존재하지 않습니다");
        }

        Collections.sort(post, Comparator.comparingInt(Post::getView).reversed());
        List<Most5PostDto> most5PostDtos = new ArrayList<>();
        Integer top5views=0;

        for(int i=0; i<5; i++){
            if (i >= post.size()) {
                break;
            }
            Most5PostDto most5PostDto = Most5PostDto.builder()
                        .title(post.get(i).getTitle())
                        .views(post.get(i).getView())
                        .build();

            top5views += post.get(i).getView();

            most5PostDtos.add(most5PostDto);
        }
        StatisticsDto statisticsDto= StatisticsDto.builder()
                    .most5PostDtos(most5PostDtos)
                    .total(top5views)
                    .build();
            return statisticsDto;
    }

}
