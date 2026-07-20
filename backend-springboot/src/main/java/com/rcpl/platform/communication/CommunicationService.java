package com.rcpl.platform.communication;

import java.util.List;

import com.rcpl.platform.audit.AuditService;
import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.communication.CommunicationDtos.MessageDto;
import com.rcpl.platform.communication.CommunicationDtos.NudgeRequest;
import com.rcpl.platform.communication.CommunicationDtos.PostMessageRequest;
import com.rcpl.platform.communication.CommunicationDtos.RequestInfoRequest;
import com.rcpl.platform.communication.CommunicationDtos.ThreadDto;
import com.rcpl.platform.communication.entity.CommMessage;
import com.rcpl.platform.communication.entity.CommThread;
import com.rcpl.platform.communication.repository.CommMessageRepository;
import com.rcpl.platform.communication.repository.CommThreadRepository;
import com.rcpl.platform.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Case/partner communication threads: read, post, nudge a partner, request info from the ASM. */
@Service
public class CommunicationService {

    private final CommThreadRepository threadRepo;
    private final CommMessageRepository messageRepo;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public CommunicationService(CommThreadRepository threadRepo, CommMessageRepository messageRepo,
                                NotificationService notificationService, AuditService auditService) {
        this.threadRepo = threadRepo;
        this.messageRepo = messageRepo;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ThreadDto> listThreads() {
        return threadRepo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ThreadDto getThread(String code) {
        return toDto(threadRepo.findById(code)
                .orElseThrow(() -> new ApiException.NotFound("Thread not found: " + code)));
    }

    @Transactional
    public ThreadDto postMessage(String code, PostMessageRequest req) {
        CommThread thread = threadRepo.findById(code)
                .orElseThrow(() -> new ApiException.NotFound("Thread not found: " + code));
        // Clear "next replier" on prior messages, then append.
        for (CommMessage m : messageRepo.findByThreadCodeOrderByCreatedAtAsc(code)) {
            if (m.isNextReplier()) {
                m.setNextReplier(false);
                messageRepo.save(m);
            }
        }
        append(code, req.authorRole(), req.authorName(), req.body(), false);
        thread.setLastMessage(req.body());
        threadRepo.save(thread);
        return toDto(thread);
    }

    @Transactional
    public ThreadDto nudge(CurrentUser user, String code, NudgeRequest req) {
        CommThread thread = upsertThread(code, req.town(), req.partnerName(), "partner");
        append(code, user.roleCode(), user.name(), req.reason(), false);
        thread.setLastMessage(req.reason());
        threadRepo.save(thread);
        auditService.logHuman(user.name(), "Nudged partner", code);
        return toDto(thread);
    }

    @Transactional
    public ThreadDto requestInfo(String code, RequestInfoRequest req) {
        CommThread thread = upsertThread(code, req.town(), req.partnerName(), "internal");
        append(code, req.reviewerRole(), req.reviewerName(), req.note(), false);
        thread.setLastMessage(req.note());
        threadRepo.save(thread);
        notificationService.push("Info requested on " + code,
                req.partnerName() + " — " + req.reviewerName() + " needs more information. Reply in the case thread.",
                "/communication", "ase_asm");
        auditService.logHuman(req.reviewerName(), "Requested info from ASM", code);
        return toDto(thread);
    }

    /** Open (create if needed) a case-discussion thread and return it (mirrors openCaseDiscussion). */
    @Transactional
    public ThreadDto ensureThread(String code, String town, String partnerName, String audience) {
        CommThread thread = upsertThread(code, town, partnerName, audience == null ? "internal" : audience);
        return toDto(thread);
    }

    /** Append a partner-facing message to a thread (creating it if needed). Used by grievance replies. */
    @Transactional
    public void appendPartnerMessage(String code, String town, String partnerName,
                                     String authorRole, String authorName, String body) {
        CommThread thread = upsertThread(code, town, partnerName, "partner");
        append(code, authorRole, authorName, body, false);
        thread.setLastMessage(body);
        threadRepo.save(thread);
    }

    private CommThread upsertThread(String code, String town, String partnerName, String audience) {
        return threadRepo.findById(code).map(t -> {
            t.setAudience(audience);
            return t;
        }).orElseGet(() -> {
            CommThread t = new CommThread();
            t.setCode(code);
            t.setTown(town);
            t.setPartnerName(partnerName);
            t.setAudience(audience);
            return threadRepo.save(t);
        });
    }

    private void append(String code, String role, String name, String body, boolean nextReplier) {
        CommMessage m = new CommMessage();
        m.setThreadCode(code);
        m.setAuthorRole(role);
        m.setAuthorName(name);
        m.setBody(body);
        m.setNextReplier(nextReplier);
        messageRepo.save(m);
    }

    private ThreadDto toDto(CommThread t) {
        List<MessageDto> messages = messageRepo.findByThreadCodeOrderByCreatedAtAsc(t.getCode())
                .stream().map(MessageDto::from).toList();
        return ThreadDto.from(t, messages);
    }
}
