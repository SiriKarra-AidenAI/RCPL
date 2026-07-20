package com.rcpl.platform.partner;

/** Partner directory record returned to the client (mirrors Partner in the contract). */
public record PartnerDto(
        String id,
        String legalName,
        String partnerType,
        String state,
        String town,
        String status,
        String onboardedAt,
        String discontinuedAt,
        String dbCode
) {
    public static PartnerDto from(Partner p) {
        return new PartnerDto(p.getId(), p.getLegalName(), p.getPartnerType(), p.getState(),
                p.getTown(), p.getStatus(), p.getOnboardedAt(), p.getDiscontinuedAt(), p.getDbCode());
    }
}
