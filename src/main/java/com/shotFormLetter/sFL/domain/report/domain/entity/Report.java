package com.shotFormLetter.sFL.domain.report.domain.entity;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name= "report_post_id")
    private Long reportPostId;

    @Column(name = "report_text", columnDefinition = "text")
    private String reportContent;

    @Column(name="repost_title")
    private String reportTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_Member") // 매핑할 외래키 이름
    private Member reportMember;

    @Column(name = "report_userid")
    private String reportUserId;

    @Column(name = "report_status")
    private String reportStatus;

    @ElementCollection
    @Column(name="reportUrls")
    @CollectionTable(name = "report_s3_urls", joinColumns = @JoinColumn(name = "report_id"))
    private List<String> s3Urls;

    @ElementCollection
    @Column(name="report")
    @CollectionTable(name = "reportCategory", joinColumns = @JoinColumn(name = "report_id"))
    private List<CateGory> category;

    @ElementCollection
    @Column(name="reportMessages",columnDefinition = "text")
    @CollectionTable(name = "reportCategory", joinColumns = @JoinColumn(name = "report_id"))
    private List<String> message;


    @Column(name="report_reference", length=10000)
    private String report_reference;

    @Column(name="report_count")
    private int reportCnt;
}
