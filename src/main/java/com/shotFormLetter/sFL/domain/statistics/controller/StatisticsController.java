package com.shotFormLetter.sFL.domain.statistics.controller;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.statistics.domain.dto.request.UserCustomDto;
import com.shotFormLetter.sFL.domain.statistics.domain.dto.request.UserIdDto;
import com.shotFormLetter.sFL.domain.statistics.domain.dto.response.MonthDataDto;
import com.shotFormLetter.sFL.domain.statistics.domain.service.CustomStatisticsService;
import com.shotFormLetter.sFL.domain.statistics.domain.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final CustomStatisticsService customStatisticsService;
    private final MemberService memberService;


    @GetMapping("/date")
    public ResponseEntity<?> userDateTimeLine(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody UserIdDto userIdDto){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(statisticsService.getOneDayTimeLine(userIdDto.getUserSeq()));
    }

    @GetMapping("/month")
    public ResponseEntity<?> userMonthTimeLine(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody UserIdDto userIdDto){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(statisticsService.getMonthTimeLine(userIdDto.getUserSeq()));
    }

    @GetMapping("/most")
    public ResponseEntity<?> userTop5Posts(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody UserIdDto userIdDto){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(statisticsService.getMostposts(userIdDto.getUserSeq()));
    }

    @GetMapping("/week")
    public ResponseEntity<?> userWeekTimeLine(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody UserIdDto userIdDto){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(statisticsService.getWeekTimeLine(userIdDto.getUserSeq()));
    }

    @GetMapping("/custom-view/{type}")
    public ResponseEntity<?> customTimeLine(@RequestHeader("X-AUTH-TOKEN")String token, @PathVariable("type") String type){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(customStatisticsService.getCustomData(type));
    }

    @GetMapping("/custom-day")
    public ResponseEntity<?> getUserDateCustomTimeLine(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody UserCustomDto userCustomDto){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(customStatisticsService.getCustomDay(userCustomDto));
    }

    @GetMapping("/custom-week")
    public ResponseEntity<?> getUserWeekCustomTimeLine(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody UserCustomDto userCustomDto){
        Member tokenMember=memberService.tokenMember(token);
        return ResponseEntity.ok(customStatisticsService.getCustomWeek(userCustomDto.getUserSeq(), userCustomDto.getStart(), userCustomDto.getEnd()));
    }

    @GetMapping("/custom-month")
    public ResponseEntity<?>  getUserMonthCustomTimeLine(@Valid @RequestBody UserCustomDto userCustomDto){
        return ResponseEntity.ok(customStatisticsService.getCustomMonth(userCustomDto.getUserSeq(), userCustomDto.getStart(), userCustomDto.getEnd()));
    }


}
