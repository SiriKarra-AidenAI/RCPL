package com.rcpl.platform.intake;

import java.util.Properties;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.FlagTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Background IMAP poller that ingests unseen distributor emails via the deterministic parser.
 * Disabled by default (app.intake.poll-enabled=false) so the app runs without a mailbox; enable
 * it and set IMAP_* to watch a real inbox. No AI is involved — parsing is rule-based.
 */
@Component
@ConditionalOnProperty(prefix = "app.intake", name = "poll-enabled", havingValue = "true")
public class IntakePoller {

    private static final Logger log = LoggerFactory.getLogger(IntakePoller.class);

    private final IntakeService intakeService;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String folderName;

    public IntakePoller(IntakeService intakeService,
                        @Value("${spring.mail.host}") String host,
                        @Value("${spring.mail.port}") int port,
                        @Value("${spring.mail.username}") String username,
                        @Value("${spring.mail.password}") String password,
                        @Value("${app.intake.folder:INBOX}") String folderName) {
        this.intakeService = intakeService;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.folderName = folderName;
    }

    @Scheduled(fixedDelayString = "${app.intake.poll-interval-ms:300000}")
    public void poll() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(host, port, username, password);
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            Message[] unseen = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            for (Message msg : unseen) {
                try {
                    intakeService.ingest(senderAddress(msg), msg.getSubject(), extractText(msg));
                    msg.setFlag(Flags.Flag.SEEN, true);
                } catch (Exception e) {
                    log.warn("Failed to ingest an intake email: {}", e.getMessage());
                }
            }
            folder.close(false);
            store.close();
            if (unseen.length > 0) {
                log.info("Ingested {} intake email(s).", unseen.length);
            }
        } catch (Exception e) {
            log.warn("Intake poll skipped (mailbox unavailable): {}", e.getMessage());
        }
    }

    /** The sender's plain email address (falls back to "email" if the message has none). */
    private String senderAddress(Message msg) throws Exception {
        jakarta.mail.Address[] from = msg.getFrom();
        if (from == null || from.length == 0) return "email";
        if (from[0] instanceof jakarta.mail.internet.InternetAddress ia) return ia.getAddress();
        return from[0].toString();
    }

    /** Best-effort plain-text extraction from a message (handles simple multipart). */
    private String extractText(Part part) throws Exception {
        Object content = part.getContent();
        if (content instanceof String s) {
            return s;
        }
        if (content instanceof Multipart multipart) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                Part bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain") || bodyPart.isMimeType("text/html")) {
                    sb.append(extractText(bodyPart)).append("\n");
                }
            }
            return sb.toString();
        }
        return "";
    }
}
