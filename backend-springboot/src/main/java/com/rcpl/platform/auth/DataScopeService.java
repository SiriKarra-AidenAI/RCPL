package com.rcpl.platform.auth;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Turns a persona's row-level data scope into a JPA {@link Specification} over a state-bearing
 * entity, so list and detail queries are scoped identically. Records store the state as a code
 * (e.g. "MH"); scoping compares against the viewer's region/state via {@link GeoRegions}.
 */
@Service
public class DataScopeService {

    private final PermissionService permissionService;

    public DataScopeService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * The set of allowed state codes for this user + entity scope, or {@code null} when
     * unrestricted ('all'). An empty set means the persona can see nothing (misconfigured —
     * scoped without the region/state the scope needs).
     */
    public Set<String> allowedStateCodes(CurrentUser user, String entity) {
        String scope = permissionService.dataScope(user.roleCode(), entity);
        if ("all".equals(scope)) {
            return null;
        }
        if ("own_state".equals(scope)) {
            if (user.state() == null) return Set.of();
            return GeoRegions.NAME_BY_CODE.entrySet().stream()
                    .filter(e -> e.getValue().equals(user.state()))
                    .map(java.util.Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }
        // own_region
        if (user.region() == null) return Set.of();
        return GeoRegions.REGION_OF.entrySet().stream()
                .filter(e -> e.getValue().equals(user.region()))
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /** Specification filtering {@code stateAttr} by the user's allowed state codes for the entity. */
    public <T> Specification<T> byState(CurrentUser user, String entity, String stateAttr) {
        Set<String> codes = allowedStateCodes(user, entity);
        return (root, query, cb) -> {
            if (codes == null) return cb.conjunction();       // 'all' — no restriction
            if (codes.isEmpty()) return cb.disjunction();     // scoped but nothing visible
            return root.get(stateAttr).in(codes);
        };
    }

    /** Whether a single record's state code is visible to the user under the entity's scope. */
    public boolean isStateVisible(CurrentUser user, String entity, String stateCode) {
        Set<String> codes = allowedStateCodes(user, entity);
        return codes == null || codes.contains(stateCode);
    }
}
