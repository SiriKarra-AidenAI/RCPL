package com.rcpl.platform.template;

import java.util.List;

import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.template.PartnerTypeDtos.PartnerTypeDto;
import com.rcpl.platform.template.PartnerTypeDtos.UpsertPartnerTypeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Template engine API — partner-type config consumed across Templates, New Application, Partners,
 * Approvals and Documents. Reads are open to any authenticated user; edits require /templates manage.
 */
@RestController
@RequestMapping("/api/partner-types")
public class PartnerTypeController {

    private final PartnerTypeService service;

    public PartnerTypeController(PartnerTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<PartnerTypeDto> list() {
        return service.list();
    }

    @GetMapping("/{code}")
    public PartnerTypeDto get(@PathVariable String code) {
        return service.get(code);
    }

    @PutMapping("/{code}")
    @RequireScreen(value = "/templates", manage = true)
    public PartnerTypeDto upsert(@PathVariable String code, @Valid @RequestBody UpsertPartnerTypeRequest req) {
        return service.upsert(code, req);
    }

    @DeleteMapping("/{code}")
    @RequireScreen(value = "/templates", manage = true)
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}
