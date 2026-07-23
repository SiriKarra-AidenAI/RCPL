package com.rcpl.platform.template;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Template engine API — partner-type config consumed across Templates, New Application, Partners,
 * Approvals and Documents. Reads are open to any authenticated user; edits require /templates manage.
 */
@RestController
@RequestMapping("/api/partner-types")
public class PartnerTypeController {

    private final PartnerTypeService service;
    private final ObjectMapper objectMapper;

    public PartnerTypeController(PartnerTypeService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<PartnerTypeDto> list(@RequestParam(required = false) String search) {
        List<PartnerTypeDto> all = service.list();
        if (search == null || search.isBlank()) {
            return all;
        }
        String needle = search.trim().toLowerCase(Locale.ROOT);
        return all.stream().filter(dto -> matchesSearch(dto, needle)).toList();
    }

    private boolean matchesSearch(PartnerTypeDto dto, String needle) {
        Map<String, Object> fields = objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() { });
        return fields.values().stream()
                .filter(java.util.Objects::nonNull)
                .anyMatch(value -> value.toString().toLowerCase(Locale.ROOT).contains(needle));
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
