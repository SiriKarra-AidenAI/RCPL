package com.rcpl.platform.mail;

import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.intake.entity.IntakeDocOverride;
import com.rcpl.platform.intake.repository.IntakeDocOverrideRepository;
import com.rcpl.platform.mail.MailDtos.MailReplyRequest;
import com.rcpl.platform.mail.MailDtos.MailReplyResult;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Outbound email sending for reply/notification flows (Intake Review "request missing info",
 * New Application onboarding e-signature, Approvals correspondence).
 *
 * <p>Builds its OWN {@link JavaMailSenderImpl} from {@code app.mail.smtp.*}, deliberately NOT the
 * Boot-autoconfigured {@code JavaMailSender} bean bound to {@code spring.mail.*} — those properties
 * are already claimed by the IMAP intake poller ({@code IntakePoller}/{@code IntakeService}) for
 * reading a distributor inbox, not sending mail. Reusing that bean here would silently try to send
 * over the IMAP host/port.
 *
 * <p>Disabled by default ({@code app.mail.smtp.enabled=false}), mirroring the same
 * "off by default so the app runs without real credentials" pattern used for
 * {@code app.intake.poll-enabled} — the send is skipped (just logged) and the call still reports
 * success, so the UI's happy path works in dev without real SMTP creds configured.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final IntakeDocOverrideRepository overrideRepo;
    private final JavaMailSenderImpl mailSender;
    private final String from;
    private final boolean enabled;

    public MailService(IntakeDocOverrideRepository overrideRepo,
                       @Value("${app.mail.smtp.host:smtp.example.com}") String host,
                       @Value("${app.mail.smtp.port:587}") int port,
                       @Value("${app.mail.smtp.username:}") String username,
                       @Value("${app.mail.smtp.password:}") String password,
                       @Value("${app.mail.smtp.from:no-reply@rcpl.example.com}") String from,
                       @Value("${app.mail.smtp.enabled:false}") boolean enabled) {
        this.overrideRepo = overrideRepo;
        this.from = from;
        this.enabled = enabled;

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        this.mailSender = sender;
    }

    /** Send a reply/notification email, optionally attaching named intake documents. */
    public MailReplyResult reply(MailReplyRequest req) {
        if (!enabled) {
            log.info("SMTP sending is disabled (app.mail.smtp.enabled=false) — would have sent mail to {} with subject '{}'",
                    req.to(), req.subject());
            return new MailReplyResult(true);
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(req.to());
            helper.setSubject(req.subject() == null ? "" : req.subject());
            helper.setText(req.text(), false);

            if (req.itemId() != null && req.attachDocs() != null) {
                for (String docName : req.attachDocs()) {
                    attach(helper, req.itemId(), docName);
                }
            }

            mailSender.send(message);
            return new MailReplyResult(true);
        } catch (Exception e) {
            log.warn("Failed to send mail to {}: {}", req.to(), e.getMessage());
            throw new ApiException.BadRequest("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Attaches one named document's stored bytes, if any override is on file. Missing/undecodable
     * docs are skipped (logged) rather than failing the whole send — decoding of the stored
     * {@code data:<mime>;base64,<bytes>} URL mirrors IntakeService#attachment(), duplicated here
     * (rather than called) since that method looks up by file NAME and throws NotFound, whereas a
     * missing doc here is an expected, non-fatal case keyed by doc NAME.
     */
    private void attach(MimeMessageHelper helper, String itemId, String docName) {
        Optional<IntakeDocOverride> override = overrideRepo.findByIntakeIdAndDocName(itemId, docName);
        if (override.isEmpty()) {
            log.info("No stored document named '{}' on intake {} — skipping attachment", docName, itemId);
            return;
        }
        String dataUrl = override.get().getDataUrl();
        if (dataUrl == null || !dataUrl.startsWith("data:")) {
            log.info("Document '{}' on intake {} has no stored bytes — skipping attachment", docName, itemId);
            return;
        }
        try {
            int comma = dataUrl.indexOf(',');
            String header = dataUrl.substring(5, comma); // "<mime>;base64"
            String mime = header.contains(";") ? header.substring(0, header.indexOf(';')) : header;
            byte[] bytes = Base64.getDecoder().decode(dataUrl.substring(comma + 1));
            String fileName = override.get().getFileName() != null ? override.get().getFileName() : docName;
            helper.addAttachment(fileName, new ByteArrayResource(bytes), mime.isBlank() ? "application/octet-stream" : mime);
        } catch (Exception e) {
            log.warn("Failed to attach document '{}' on intake {}: {} — skipping attachment", docName, itemId, e.getMessage());
        }
    }
}
