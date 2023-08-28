package com.shotFormLetter.sFL.domain.statistics.domain.service;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotAccessException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotFoundException;
import com.shotFormLetter.sFL.ExceptionHandler.DataNotMatchException;
import com.shotFormLetter.sFL.domain.member.repository.MemberRepository;
import com.shotFormLetter.sFL.domain.statistics.domain.dto.request.UserCustomDto;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomStatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final MemberRepository memberRepository;


    public TimeLineDto getCustomData(String type){
        LocalDate endTime=LocalDate.now();
        LocalDate startTime=LocalDate.now();

        String nowType=null;
        String notice="";
        String start="";
        String end="";
        if(type.equals("day")){
            endTime = endTime.minusDays(1);
            startTime = endTime.minusMonths(3);
            start=startTime.toString();
            end=endTime.toString();
            nowType=type;
            notice = "일간 통계는 당일 이전 까지만 가능합니다";
        }else if (type.equals("week")){
            endTime=endTime.minusWeeks(1).with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
            startTime = endTime.minusMonths(3);
            start=startTime.toString();
            end=endTime.toString();
            nowType=type;
            notice ="주간 통계는 당일 이전주 까지만 가능합니다";
        } else if(type.equals("month")){
            endTime=endTime.minusMonths(1);
            startTime = endTime.minusYears(2);
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM");
            start = startTime.format(formatter);
            end =endTime.format(formatter);
            nowType =type;
            notice="월간 통계는 당월 이전 달까지만 가능합니다";
        } else {
            throw new DataNotFoundException("일간/주간/월간 을 선택해주세요");
        }
        TimeLineDto timeLineDto = TimeLineDto.builder()
                .start(start)
                .end(end)
                .type(nowType)
                .notice(notice)
                .build();
        return timeLineDto;
    }



    public List<OneDayDataDto> getCustomDay(UserCustomDto userCustomDto){
        Long userSeq= userCustomDto.getUserSeq();
        List<Statistics> statistics= statisticsRepository.getListByMemberId(userSeq);

        if(statistics==null){
            throw new DataNotFoundException("게시글 이 없어 통계를 잡을 수 없습니다");
        }
        LocalDate localDate=LocalDate.now();
        LocalDate endDate=LocalDate.parse(userCustomDto.getEnd());
        LocalDate startDate=LocalDate.parse(userCustomDto.getStart());


        if(endDate==localDate){
            throw new DataNotMatchException("당일의 이전 날까지만 통계집계가 가능합니다");
        }

        if(ChronoUnit.MONTHS.between(startDate,endDate)>3){
            throw new DataNotAccessException("일간은 3개월 까지 통계집가가 가능합니다");
        }

        if(endDate.isAfter(localDate)){
            throw new DataNotMatchException("마지막 날짜는 당일을 넘길 수 없습니다");
        }

        if(endDate.isBefore(localDate.minusMonths(3)) || startDate.isBefore(localDate.minusMonths(3))){
            throw new DataNotMatchException("통계 집계 가능한 범위가 아닙니다");
        }

        List<OneDayDataDto> oneDayDataDtoList = new ArrayList<>();

        List<String> getTimeLine= new ArrayList<>();
        for(Statistics s : statistics){
            List<String> timeLane = s.getViewTime();
            for(String t : timeLane){
                LocalDateTime dateTime=LocalDateTime.parse(t);
                getTimeLine.add(dateTime.toLocalDate().toString());
            }
        }


        while(!startDate.isAfter(endDate)){
            OneDayDataDto oneDayDataDto =new OneDayDataDto();
            oneDayDataDto.setDate(startDate.toString());
            System.out.println(startDate.toString());
            Integer cnt = Collections.frequency(getTimeLine,startDate.toString());
            oneDayDataDto.setCount(cnt);
            oneDayDataDtoList.add(oneDayDataDto);
            startDate=startDate.plusDays(1);
            System.out.println(startDate);
        }
        return oneDayDataDtoList;
    }


    public List<MonthDataDto> getCustomMonth(Long userSeq,String start,String end){

        List<Statistics> statistics= statisticsRepository.getListByMemberId(userSeq);

        if(statistics==null){
            throw new DataNotFoundException("게시글이 없어 통계를 잡을 수 없습니다");
        }
        LocalDate localDate=LocalDate.now();
        LocalDate endMonth=LocalDate.parse(end+"-01");
        LocalDate startMonth=LocalDate.parse(start+"-01");

        if(endMonth.getMonthValue()==localDate.getMonthValue() && endMonth.getYear()==localDate.getYear()){
            throw new DataNotMatchException("당월 까지의 통계는 잡히지 않습니다");
        } if(endMonth.isAfter(localDate)){
            throw new DataNotMatchException("마지막 날짜는 당월을 넘길 수 없습니다");
        }

        if(ChronoUnit.MONTHS.between(startMonth,endMonth)>24){
            throw new DataNotAccessException("월간은 2년 까지만 조회가 가능합니다");
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

        List<MonthDataDto> monthDataDtos = new ArrayList<>();

        while(startMonth.isBefore(endMonth) || startMonth.equals(endMonth)){
            MonthDataDto monthDataDto=new MonthDataDto();
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM");
            String month= startMonth.format(formatter);
            Integer cnt = Collections.frequency(getMonthTimeLane,month);
            monthDataDto.setMonth(month);
            monthDataDto.setCount(cnt);
            monthDataDtos.add(monthDataDto);
            startMonth = startMonth.plusMonths(1);
        }
        return monthDataDtos;
    }

    public List<WeekDataDto> getCustomWeek(Long userSeq,String start,String end){
        List<Statistics> statistics= statisticsRepository.getListByMemberId(userSeq);

        if(statistics==null){
            throw new DataNotFoundException("게시글 이 없어 통계를 잡을 수 없습니다");
        }
        LocalDate localDate=LocalDate.now();
        LocalDate endWeek=LocalDate.parse(end);
        LocalDate startWeek=LocalDate.parse(start);
        if(endWeek==localDate){
            throw new DataNotMatchException("해당일의 이전 주 까지만 통계집계가 가능합니다");
        }

        DayOfWeek startDay=startWeek.getDayOfWeek();
        DayOfWeek endDay=endWeek.getDayOfWeek();
        if(startDay != DayOfWeek.MONDAY || endDay != DayOfWeek.MONDAY){
            throw new DataNotMatchException("시작일과 끝나는 지점은 월요일 이어야합니다");
        }


        if(ChronoUnit.MONTHS.between(startWeek,endWeek)>3){
            throw new DataNotAccessException("주간은 3개월 까지 통계집가가 가능합니다");
        }

        if(endWeek.isAfter(localDate)){
            throw new DataNotMatchException("마지막 날짜는 당일을 넘길 수 없습니다");
        }

        if(endWeek.isBefore(localDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).minusMonths(3))
           || startWeek.isAfter(endWeek)){
            throw new DataNotMatchException("통계범위를 벗어 났습니다");
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
//        startWeek.isBefore(endWeek) || startWeek==endWeek
        while(startWeek.isBefore(endWeek.plusDays(6))) {

            WeekDataDto weekDataDto = new WeekDataDto();
            LocalDate last = startWeek.plusDays(6);
            System.out.println(startWeek);
            Integer count = 0;
            Integer year = startWeek.getYear();
            Integer month = startWeek.getMonthValue();
            weekDataDto.setYear(year.toString());
            weekDataDto.setMonth(month.toString());
            weekDataDto.setStart(startWeek.toString());
            weekDataDto.setEnd(last.toString());

            for (String weekTime : getWeekTimeLane) {
                LocalDate wt = LocalDate.parse(weekTime);
                if ((wt.isEqual(startWeek) || wt.isAfter(startWeek)) && (wt.isEqual(last) || wt.isBefore(last))) {
                    count = count + 1;
                }
            }
            weekDataDto.setCount(count);
            weekDataDtos.add(weekDataDto);
            startWeek=startWeek.plusDays(7);
        }

        return weekDataDtos;

    }

}
