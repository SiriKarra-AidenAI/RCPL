package com.rcpl.platform.intake;

import com.rcpl.platform.intake.IntakeDtos.InboxStatusDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Mailbox connectivity status for the Intake Inbox banner (separate path from /api/intake). */
@RestController
public class InboxStatusController {

    private final IntakeService intakeService;

    public InboxStatusController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @GetMapping("/api/inbox/status")
    public InboxStatusDto status() {
        return intakeService.inboxStatus();
    }
}
