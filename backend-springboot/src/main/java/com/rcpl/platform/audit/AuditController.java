package com.rcpl.platform.audit;

import java.util.List;

import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.audit.AuditDtos.AppendAuditRequest;
import com.rcpl.platform.audit.AuditDtos.AuditDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Live audit trail — read (screen-gated) and append. */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditRepository auditRepository;
    private final AuditService auditService;

    public AuditController(AuditRepository auditRepository, AuditService auditService) {
        this.auditRepository = auditRepository;
        this.auditService = auditService;
    }

    @GetMapping
    @RequireScreen("/audit-log")
    public List<AuditDto> list() {
        return auditRepository.findAllByOrderByCreatedAtDesc().stream().map(AuditDto::from).toList();
    }

    @PostMapping
    public AuditDto append(@Valid @RequestBody AppendAuditRequest req) {
        return AuditDto.from(auditService.log(req.actor(), req.kind(), req.action(), req.entity()));
    }
}
