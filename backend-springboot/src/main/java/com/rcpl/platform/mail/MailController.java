package com.rcpl.platform.mail;

import com.rcpl.platform.mail.MailDtos.MailReplyRequest;
import com.rcpl.platform.mail.MailDtos.MailReplyResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Outbound email API. Cross-cutting — called from Intake Review, New Application, and Approvals
 * alike, so this only requires plain authentication (no {@code @RequireScreen}), not one screen's
 * permission.
 */
@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/reply")
    public MailReplyResult reply(@Valid @RequestBody MailReplyRequest req) {
        return mailService.reply(req);
    }
}
