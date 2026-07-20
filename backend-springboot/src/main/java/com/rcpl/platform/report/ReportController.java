package com.rcpl.platform.report;

import java.util.List;

import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.common.DateLabels;
import com.rcpl.platform.common.Ids;
import com.rcpl.platform.report.ReportDtos.CreateReportRequest;
import com.rcpl.platform.report.ReportDtos.ReportDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Leadership reports API. */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository reportRepository;

    public ReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping
    @RequireScreen("/reports")
    public List<ReportDto> list() {
        return reportRepository.findAllByOrderByCreatedAtDesc().stream().map(ReportDto::from).toList();
    }

    @PostMapping
    @RequireScreen(value = "/reports", manage = true)
    public ReportDto create(@Valid @RequestBody CreateReportRequest req) {
        Report r = new Report();
        r.setId(Ids.newId("rep"));
        r.setName(req.name());
        r.setFormat(req.format());
        r.setDateLabel(DateLabels.dateStamp());
        return ReportDto.from(reportRepository.save(r));
    }
}
