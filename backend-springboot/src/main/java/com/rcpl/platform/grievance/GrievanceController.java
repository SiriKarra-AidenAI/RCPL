package com.rcpl.platform.grievance;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.grievance.GrievanceDtos.CreateGrievanceRequest;
import com.rcpl.platform.grievance.GrievanceDtos.GrievanceDto;
import com.rcpl.platform.grievance.GrievanceDtos.StatusRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Grievances queue API. */
@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceService grievanceService;

    public GrievanceController(GrievanceService grievanceService) {
        this.grievanceService = grievanceService;
    }

    @GetMapping
    @RequireScreen("/grievances")
    public List<GrievanceDto> list() {
        return grievanceService.list();
    }

    @GetMapping("/{id}")
    @RequireScreen("/grievances")
    public GrievanceDto get(@PathVariable String id) {
        return grievanceService.get(id);
    }

    @PostMapping
    @RequireScreen(value = "/grievances", manage = true)
    public GrievanceDto create(@Valid @RequestBody CreateGrievanceRequest req) {
        return grievanceService.create(req);
    }

    @PatchMapping("/{id}")
    @RequireScreen(value = "/grievances", manage = true)
    public GrievanceDto updateStatus(@PathVariable String id, @Valid @RequestBody StatusRequest req) {
        return grievanceService.updateStatus(id, req.status());
    }

    @PostMapping("/{id}/update")
    @RequireScreen(value = "/grievances", manage = true)
    public GrievanceDto sendUpdate(@AuthenticationPrincipal CurrentUser user, @PathVariable String id) {
        return grievanceService.sendUpdate(user, id);
    }
}
