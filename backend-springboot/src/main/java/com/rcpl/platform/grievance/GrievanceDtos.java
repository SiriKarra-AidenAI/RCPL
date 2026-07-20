package com.rcpl.platform.grievance;

import java.util.List;

import com.rcpl.platform.grievance.entity.Grievance;
import com.rcpl.platform.grievance.entity.GrievanceUpdate;
import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for grievances. */
public final class GrievanceDtos {

    private GrievanceDtos() {}

    public record UpdateDto(Long id, String on, String by, String note) {
        public static UpdateDto from(GrievanceUpdate u) {
            return new UpdateDto(u.getId(), u.getOnDate(), u.getByActor(), u.getNote());
        }
    }

    /** Field names/order mirror the frontend's Grievance contract (mock/grievances.ts). */
    public record GrievanceDto(
            String id, String distributor, String town, String channel, String category, String priority,
            String status, String subject, String detail, String raisedOn, long ageDays, String ownerRole,
            String slaLabel, boolean isOverdue, List<UpdateDto> updates) {

        public static GrievanceDto from(Grievance g, List<UpdateDto> updates) {
            return new GrievanceDto(g.getId(), g.getDistributor(), g.getTown(), g.getChannel(), g.getCategory(),
                    g.getPriority(), g.getStatus(), g.getSubject(), g.getDetail(), g.getRaisedOn(),
                    com.rcpl.platform.common.DateLabels.daysSince(g.getRaisedOn()), g.getOwnerRole(),
                    g.getSlaLabel(), g.isOverdue(), updates);
        }
    }

    public record CreateGrievanceRequest(
            @NotBlank String distributor, String town, String channel, String category, String priority,
            @NotBlank String subject, String detail, String ownerRole, String slaLabel) {
    }

    public record StatusRequest(@NotBlank String status) { // open | in_progress | resolved
    }
}
