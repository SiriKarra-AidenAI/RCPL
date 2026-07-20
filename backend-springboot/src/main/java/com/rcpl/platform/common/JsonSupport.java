package com.rcpl.platform.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/** Small helper for the JSON-text columns (discontinuation form, intake raw payload). */
@Component
public class JsonSupport {

    private final ObjectMapper mapper;

    public JsonSupport(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /** Parse stored JSON text to a node, or null if blank/invalid. */
    public JsonNode parse(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    /** Serialize a node/object to JSON text, or null if the value is null. */
    public String write(Object value) {
        if (value == null) return null;
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new ApiException.BadRequest("Invalid JSON payload");
        }
    }
}
