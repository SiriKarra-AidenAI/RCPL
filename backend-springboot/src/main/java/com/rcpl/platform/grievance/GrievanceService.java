package com.rcpl.platform.grievance;

import java.util.List;

import com.rcpl.platform.audit.AuditService;
import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.DateLabels;
import com.rcpl.platform.common.Ids;
import com.rcpl.platform.communication.CommunicationService;
import com.rcpl.platform.grievance.GrievanceDtos.CreateGrievanceRequest;
import com.rcpl.platform.grievance.GrievanceDtos.GrievanceDto;
import com.rcpl.platform.grievance.GrievanceDtos.UpdateDto;
import com.rcpl.platform.grievance.entity.Grievance;
import com.rcpl.platform.grievance.entity.GrievanceUpdate;
import com.rcpl.platform.grievance.repository.GrievanceRepository;
import com.rcpl.platform.grievance.repository.GrievanceUpdateRepository;
import com.rcpl.platform.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Grievances queue: read, status changes, and a distributor holding-reply that lands in Communication. */
@Service
public class GrievanceService {

    private static final String HOLDING_REPLY =
            "Our team is actively looking into it and will get back to you once the review is complete.";

    private final GrievanceRepository grievanceRepo;
    private final GrievanceUpdateRepository updateRepo;
    private final CommunicationService communicationService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public GrievanceService(GrievanceRepository grievanceRepo, GrievanceUpdateRepository updateRepo,
                            CommunicationService communicationService, NotificationService notificationService,
                            AuditService auditService) {
        this.grievanceRepo = grievanceRepo;
        this.updateRepo = updateRepo;
        this.communicationService = communicationService;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<GrievanceDto> list() {
        return list(null);
    }

    /** Grievances queue, optionally filtered by status tab so the screen can show a tab-specific empty state. */
    @Transactional(readOnly = true)
    public List<GrievanceDto> list(String status) {
        return grievanceRepo.findAll().stream()
                .filter(g -> status == null || status.isBlank() || status.equals(g.getStatus()))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GrievanceDto get(String id) {
        return toDto(require(id));
    }

    @Transactional
    public GrievanceDto create(CreateGrievanceRequest req) {
        Grievance g = new Grievance();
        g.setId(Ids.newId("grv"));
        g.setDistributor(req.distributor());
        g.setTown(req.town());
        g.setChannel(req.channel() == null ? "Email" : req.channel());
        g.setCategory(req.category());
        g.setPriority(req.priority() == null ? "medium" : req.priority());
        g.setSubject(req.subject());
        g.setDetail(req.detail());
        g.setOwnerRole(req.ownerRole() == null ? "channel_dev" : req.ownerRole());
        g.setStatus("open");
        g.setSlaLabel(req.slaLabel());
        g.setRaisedOn(DateLabels.dateStamp());
        return toDto(grievanceRepo.save(g));
    }

    @Transactional
    public GrievanceDto updateStatus(String id, String status) {
        Grievance g = require(id);
        if (g.getStatus().equals(status)) {
            return toDto(g);
        }
        String label = switch (status) {
            case "open" -> "Reopened";
            case "in_progress" -> "Marked in progress";
            case "resolved" -> "Resolved";
            default -> throw new ApiException.BadRequest("Unknown status: " + status);
        };
        g.setStatus(status);
        if ("resolved".equals(status)) {
            g.setSlaLabel("Closed");
            g.setOverdue(false);
        }
        grievanceRepo.save(g);
        addUpdate(id, "You", label + " from the Grievances queue.");
        return toDto(g);
    }

    /** Send the distributor a holding reply — lands in Communication as a partner thread. */
    @Transactional
    public GrievanceDto sendUpdate(CurrentUser user, String id) {
        Grievance g = require(id);
        if ("open".equals(g.getStatus())) {
            g.setStatus("in_progress");
            grievanceRepo.save(g);
        }
        addUpdate(id, "You", "Emailed distributor: \"" + HOLDING_REPLY + "\"");
        communicationService.appendPartnerMessage(id, g.getTown(), g.getDistributor(),
                g.getOwnerRole(), "You", HOLDING_REPLY);
        notificationService.push("Distributor emailed",
                g.getDistributor() + " was sent a holding reply on " + id + ".", "/grievances", g.getOwnerRole());
        auditService.logHuman(user.name(), "Emailed distributor a holding reply", id);
        return toDto(g);
    }

    private void addUpdate(String grievanceId, String by, String note) {
        GrievanceUpdate u = new GrievanceUpdate();
        u.setGrievanceId(grievanceId);
        u.setOnDate(DateLabels.dateStamp());
        u.setByActor(by);
        u.setNote(note);
        updateRepo.save(u);
    }

    private Grievance require(String id) {
        return grievanceRepo.findById(id)
                .orElseThrow(() -> new ApiException.NotFound("Grievance not found: " + id));
    }

    private GrievanceDto toDto(Grievance g) {
        List<UpdateDto> updates = updateRepo.findByGrievanceIdOrderByIdAsc(g.getId())
                .stream().map(UpdateDto::from).toList();
        return GrievanceDto.from(g, updates);
    }
}
