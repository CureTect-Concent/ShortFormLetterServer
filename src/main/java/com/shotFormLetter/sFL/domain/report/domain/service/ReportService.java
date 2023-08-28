package com.shotFormLetter.sFL.domain.report.domain.service;

import com.shotFormLetter.sFL.ExceptionHandler.DataNotAccessException;

import com.shotFormLetter.sFL.domain.admin.domain.service.AdminService;

import com.shotFormLetter.sFL.domain.post.domain.dto.response.MessageDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.post.domain.repository.PostRepository;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import com.shotFormLetter.sFL.domain.report.domain.dto.request.ReportForm;
import com.shotFormLetter.sFL.domain.report.domain.dto.request.ReportId;
import com.shotFormLetter.sFL.domain.report.domain.dto.response.ReportCateGory;
import com.shotFormLetter.sFL.domain.report.domain.dto.response.ReportList;
import com.shotFormLetter.sFL.domain.report.domain.entity.CateGory;
import com.shotFormLetter.sFL.domain.report.domain.entity.Report;
import com.shotFormLetter.sFL.domain.report.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final AdminService adminService;
    private final s3UploadService s3UploadService;
    public MessageDto RequestReport(ReportForm reportForm){
        Report report = reportRepository.getByReportPostId(reportForm.getPostId());
        MessageDto messageDto = new MessageDto();

        if(report != null){
            int cnt = report.getReportCnt()+1;
            List<CateGory> category = report.getCategory();
            CateGory reportCateGory = CateGory.fromValue(reportForm.getCateGory());
            List<String> message = report.getMessage();
            category.add(reportCateGory);
            message.add(reportForm.getMessage());
            report.setMessage(message);
            report.setCategory(category);
            report.setReportCnt(cnt);
            reportRepository.save(report);
            messageDto.setMessage("신고 처리가 완료되었습니다");
            System.out.println("여기서 끝남");
            return messageDto;
        }

        Post post = postRepository.getPostByPostId(reportForm.getPostId());

        if(post == null){
            throw new DataNotAccessException("삭제된 게시글은 수정할 수 없습니다");
        }

        List<String> urls = post.getS3Urls();
        List<String> geturls=new ArrayList<>();
        if(urls.size()!=0){
            for(String url:urls){
                int index=url.indexOf("/", "https://".length());
                url=url.substring(index);
                String key= "report" + url;

                System.out.println(url);
                System.out.println(key);
                String result = s3UploadService.CopyImage(url,key);
                geturls.add(result);
            }
        }

        List<CateGory> category = new ArrayList<>();
        CateGory reportCateGory = CateGory.fromValue(reportForm.getCateGory());
        category.add(reportCateGory);
        List<String> message = new ArrayList<>();
        message.add(reportForm.getMessage());
        report = Report.builder()
                .reportPostId(post.getPostId())
                .reportTitle(post.getTitle())
                .reportContent(post.getTitle())
                .reportMember(post.getMember())
                .reportUserId(post.getUserId())
                .reportStatus("검토중")
                .report_reference(post.getMedia_reference())
                .s3Urls(geturls)
                .category(category)
                .message(message)
                .reportCnt(1)
                .build();
        reportRepository.save(report);
        messageDto.setMessage("신고 처리가 완료되었습니다");
        return messageDto;
    }

    public List<ReportList> getList(String token){
        adminService.checkValidAdmin(token);
        List<Report> all = reportRepository.findAll();
        List<ReportList> reportLists = new ArrayList<>();
        for(Report report:all){
            ReportList reportList = ReportList.builder()
                    .reportId(report.getReportId().toString())
                    .user(report.getReportUserId())
                    .reportCount(report.getReportCnt())
                    .title(report.getReportTitle())
                    .build();
            reportLists.add(reportList);
        }
        return reportLists;
    }

    public ReportCateGory getCategory(){
        List<String> categoryValues = CateGory.getCategoryValues();
        ReportCateGory reportCateGory = ReportCateGory.builder()
                .categoryValues(categoryValues)
                .build();

        return reportCateGory;
    }



    public void deleteReport(ReportId reportId,String token){
        adminService.checkValidAdmin(token);
        List<Report> report = reportRepository.getByReportId(reportId.getReportId());
        for(Report re:report){
            reportRepository.delete(re);
        }
    }
}
