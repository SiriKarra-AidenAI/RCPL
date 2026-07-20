package com.rcpl.platform.audit;

import com.rcpl.platform.common.DateLabels;
import com.rcpl.platform.common.Ids;
import org.springframework.stereotype.Service;

/**
 * Single call site for appending audit rows. State-changing services call this inside their
 * own transaction so the trail is always consistent with the change.
 */
@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public AuditEntry log(String actor, String kind, String action, String entity) {
        AuditEntry e = new AuditEntry();
        e.setId(Ids.newId("a"));
        e.setActor(actor);
        e.setKind(kind == null ? "human" : kind);
        e.setAction(action);
        e.setEntity(entity);
        e.setWhenLabel(DateLabels.auditStamp());
        return auditRepository.save(e);
    }

    /** Convenience for human-actor actions. */
    public AuditEntry logHuman(String actor, String action, String entity) {
        return log(actor, "human", action, entity);
    }
}
