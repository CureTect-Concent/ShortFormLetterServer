package com.shotFormLetter.sFL.domain.report.domain.repository;

import com.shotFormLetter.sFL.domain.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Long> {

    Report findByReportId(Long reportId);
    Report getByReportPostId(Long reportPostId);
    List<Report> getByReportId(Long reportId);
    List<Report> findAll();
}
