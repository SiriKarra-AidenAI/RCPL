package com.rcpl.platform.gtm;

import java.util.Map;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.gtm.GtmDtos.CoverageResponse;
import com.rcpl.platform.gtm.GtmDtos.ImportRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** GTM coverage API — region-scoped coverage tree + factors, plus admin bulk import. */
@RestController
@RequestMapping("/api/gtm-coverage")
public class GtmController {

    private final GtmService gtmService;

    public GtmController(GtmService gtmService) {
        this.gtmService = gtmService;
    }

    @GetMapping
    @RequireScreen("/gtm-coverage")
    public CoverageResponse coverage(@AuthenticationPrincipal CurrentUser user) {
        return gtmService.coverage(user);
    }

    @PostMapping("/import")
    @RequireScreen(value = "/gtm-coverage", manage = true)
    public Map<String, Integer> importCoverage(@RequestBody ImportRequest req) {
        return Map.of("importedStates", gtmService.importStates(req.states()));
    }
}
