package com.shotFormLetter.sFL.domain.report.controller;


import com.shotFormLetter.sFL.domain.member.service.MemberService;
import com.shotFormLetter.sFL.domain.report.domain.dto.request.ReportForm;
import com.shotFormLetter.sFL.domain.report.domain.dto.request.ReportId;
import com.shotFormLetter.sFL.domain.report.domain.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    @GetMapping()
    public ResponseEntity<?> callReport(){
        return ResponseEntity.ok(reportService.getCategory());
    }


    @GetMapping("/list")
    public ResponseEntity<?> callReport(@RequestHeader("X-AUTH-TOKEN")String token){
        return ResponseEntity.ok(reportService.getList(token));
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendReport(@RequestBody ReportForm reportForm){
        return ResponseEntity.ok(reportService.RequestReport(reportForm));
    }

    @DeleteMapping("/delete")
    public void deleteReport(@RequestHeader("X-AUTH-TOKEN")String token,
                                          @RequestBody ReportId reportId){
        reportService.deleteReport(reportId , token);
    }


}
