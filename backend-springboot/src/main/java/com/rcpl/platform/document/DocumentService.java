package com.rcpl.platform.document;

import java.util.List;

import com.rcpl.platform.audit.AuditService;
import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.DateLabels;
import com.rcpl.platform.common.Ids;
import com.rcpl.platform.document.DocumentDtos.CreateDocumentRequest;
import com.rcpl.platform.document.DocumentDtos.DocumentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Submitted documents + deterministic (non-AI) claimed-vs-extracted verification. */
@Service
public class DocumentService {

    private final SubmittedDocumentRepository repository;
    private final AuditService auditService;

    public DocumentService(SubmittedDocumentRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> list(String caseCode) {
        List<SubmittedDocument> docs = caseCode == null || caseCode.isBlank()
                ? repository.findAll()
                : repository.findByCaseCode(caseCode);
        return docs.stream().map(DocumentDto::from).toList();
    }

    @Transactional(readOnly = true)
    public DocumentDto get(String id) {
        return DocumentDto.from(require(id));
    }

    @Transactional
    public DocumentDto create(CreateDocumentRequest req) {
        SubmittedDocument d = new SubmittedDocument();
        d.setId(Ids.newId("doc"));
        d.setCaseCode(req.caseCode());
        d.setPartnerType(req.partnerType());
        d.setDocName(req.docName());
        d.setClaimed(req.claimed());
        d.setExtracted(req.extracted());
        d.setStatus("not_checked");
        d.setFileName(req.fileName());
        d.setUploadedOn(DateLabels.dateStamp());
        d.setOptional(req.optional());
        d.setThisWeek(true);
        return DocumentDto.from(repository.save(d));
    }

    /**
     * Deterministic verification: compares claimed vs extracted text (no AI). Matches → verified,
     * differs → mismatch, missing extracted → pending.
     */
    @Transactional
    public DocumentDto verify(CurrentUser user, String id) {
        SubmittedDocument d = require(id);
        if (d.getExtracted() == null || d.getExtracted().isBlank()) {
            d.setStatus("pending");
        } else if (d.getClaimed() != null && normalize(d.getClaimed()).equals(normalize(d.getExtracted()))) {
            d.setStatus("verified");
            d.setVerifiedOn(DateLabels.dateStamp());
        } else {
            d.setStatus("mismatch");
        }
        repository.save(d);
        auditService.logHuman(user.name(), "Verified document (" + d.getStatus() + ")", d.getDocName());
        return DocumentDto.from(d);
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private SubmittedDocument require(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException.NotFound("Document not found: " + id));
    }
}
