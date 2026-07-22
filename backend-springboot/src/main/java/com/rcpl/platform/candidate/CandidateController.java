package com.rcpl.platform.candidate;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.candidate.CandidateDtos.ActivateRequest;
import com.rcpl.platform.candidate.CandidateDtos.CandidateDto;
import com.rcpl.platform.candidate.CandidateDtos.CreateCandidateRequest;
import com.rcpl.platform.candidate.CandidateDtos.DiscontinuationFormRequest;
import com.rcpl.platform.candidate.CandidateDtos.MoveStageRequest;
import com.rcpl.platform.candidate.CandidateDtos.UpdateCandidateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Candidate (lead) pipeline API. */
@RestController
@RequestMapping("/api/candidates")
@Validated
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    @RequireScreen("/leads")
    public List<CandidateDto> list() {
        return candidateService.list();
    }

    @GetMapping("/{id}")
    @RequireScreen("/leads")
    public CandidateDto get(@PathVariable @NotBlank String id) {
        return candidateService.get(id);
    }

    @PostMapping
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto create(@AuthenticationPrincipal CurrentUser user,
                               @Valid @RequestBody CreateCandidateRequest req) {
        return candidateService.create(user, req);
    }

    @PatchMapping("/{id}")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto update(@PathVariable @NotBlank String id, @Valid @RequestBody UpdateCandidateRequest req) {
        return candidateService.update(id, req);
    }

    @PostMapping("/{id}/stage")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto moveStage(@PathVariable @NotBlank String id, @Valid @RequestBody MoveStageRequest req) {
        return candidateService.moveStage(id, req.stage());
    }

    @PostMapping("/{id}/activate")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto activate(@AuthenticationPrincipal CurrentUser user, @PathVariable @NotBlank String id,
                                 @Valid @RequestBody ActivateRequest req) {
        return candidateService.activate(user, id, req);
    }

    @PostMapping("/{id}/shortlist")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto shortlist(@PathVariable @NotBlank String id,
                                  @org.springframework.web.bind.annotation.RequestParam(defaultValue = "true") boolean on) {
        return candidateService.setShortlisted(id, on);
    }

    @PostMapping("/{id}/reject")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto reject(@PathVariable @NotBlank String id) {
        return candidateService.reject(id);
    }

    @PostMapping("/{id}/reinstate")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto reinstate(@PathVariable @NotBlank String id) {
        return candidateService.reinstate(id);
    }

    @PostMapping("/{id}/evaluate")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto evaluate(@PathVariable @NotBlank String id) {
        return candidateService.evaluate(id);
    }

    @PostMapping("/{id}/discontinuation-form")
    @RequireScreen(value = "/leads", manage = true)
    public CandidateDto discontinuationForm(@PathVariable @NotBlank String id,
                                            @Valid @RequestBody DiscontinuationFormRequest req) {
        return candidateService.setDiscontinuationForm(id, req.form());
    }
}
