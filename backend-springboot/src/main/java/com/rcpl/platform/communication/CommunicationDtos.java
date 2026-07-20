package com.rcpl.platform.communication;

import java.util.List;

import com.rcpl.platform.communication.entity.CommMessage;
import com.rcpl.platform.communication.entity.CommThread;
import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for communication threads and messages. */
public final class CommunicationDtos {

    private CommunicationDtos() {}

    // id is a String (not the entity's Long PK) to match the frontend's CaseMessage.id contract.
    public record MessageDto(String id, String authorRole, String authorName, String body, boolean isNextReplier) {
        public static MessageDto from(CommMessage m) {
            return new MessageDto(String.valueOf(m.getId()), m.getAuthorRole(), m.getAuthorName(), m.getBody(), m.isNextReplier());
        }
    }

    public record ThreadDto(String code, String town, String partnerName, String audience,
                            String last, List<MessageDto> participants) {
        public static ThreadDto from(CommThread t, List<MessageDto> participants) {
            return new ThreadDto(t.getCode(), t.getTown(), t.getPartnerName(), t.getAudience(),
                    t.getLastMessage(), participants);
        }
    }

    public record PostMessageRequest(@NotBlank String authorRole, @NotBlank String authorName, @NotBlank String body) {
    }

    public record NudgeRequest(@NotBlank String town, @NotBlank String partnerName, @NotBlank String reason) {
    }

    public record RequestInfoRequest(@NotBlank String town, @NotBlank String partnerName,
                                     @NotBlank String reviewerRole, @NotBlank String reviewerName,
                                     @NotBlank String note) {
    }

    /** Open (create if needed) a case-discussion thread. */
    public record EnsureThreadRequest(@NotBlank String code, String town, String partnerName, String audience) {
    }
}
