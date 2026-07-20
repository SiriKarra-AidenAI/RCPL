package com.rcpl.platform.document;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.document.DocumentDtos.CreateDocumentRequest;
import com.rcpl.platform.document.DocumentDtos.DocumentDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Submitted documents API + deterministic verification. */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    @RequireScreen("/documents")
    public List<DocumentDto> list(@RequestParam(required = false) String caseCode) {
        return documentService.list(caseCode);
    }

    @GetMapping("/{id}")
    @RequireScreen("/documents")
    public DocumentDto get(@PathVariable String id) {
        return documentService.get(id);
    }

    @PostMapping
    @RequireScreen(value = "/documents", manage = true)
    public DocumentDto create(@Valid @RequestBody CreateDocumentRequest req) {
        return documentService.create(req);
    }

    @PostMapping("/{id}/verify")
    @RequireScreen(value = "/documents", manage = true)
    public DocumentDto verify(@AuthenticationPrincipal CurrentUser user, @PathVariable String id) {
        return documentService.verify(user, id);
    }
}
