package com.rcpl.platform.gtm;

import java.math.BigDecimal;
import java.util.List;

/** Request/response payloads for GTM coverage. */
public final class GtmDtos {

    private GtmDtos() {}

    public record DbDto(String name, String type, String status) {}

    public record AreaDto(String name, Integer target, Integer actual, List<DbDto> dbs) {}

    public record CityDto(String name, Integer target, Integer actual, List<AreaDto> areas) {}

    public record StateDto(String code, String name, String region, Integer target, Integer actual,
                           List<CityDto> cities) {}

    public record FactorDto(String key, String label, String sub, String icon,
                            BigDecimal perTarget, BigDecimal delta, boolean money, boolean extra) {}

    public record CoverageResponse(List<StateDto> states, List<FactorDto> factors) {}

    /** Bulk import of state trees (admin). Cities/areas/dbs optional per state. */
    public record ImportRequest(List<StateDto> states) {}
}
