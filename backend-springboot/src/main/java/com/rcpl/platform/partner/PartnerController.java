package com.rcpl.platform.partner;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Partner directory API (data-scoped by the persona's row-level scope). */
@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    @RequireScreen("/partners")
    public List<PartnerDto> list(@AuthenticationPrincipal CurrentUser user) {
        return partnerService.list(user);
    }

    @GetMapping("/{id}")
    @RequireScreen("/partners")
    public PartnerDto get(@AuthenticationPrincipal CurrentUser user, @PathVariable String id) {
        return partnerService.get(user, id);
    }
}
