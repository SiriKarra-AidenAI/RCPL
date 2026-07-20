package com.rcpl.platform.communication;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.communication.CommunicationDtos.EnsureThreadRequest;
import com.rcpl.platform.communication.CommunicationDtos.NudgeRequest;
import com.rcpl.platform.communication.CommunicationDtos.PostMessageRequest;
import com.rcpl.platform.communication.CommunicationDtos.RequestInfoRequest;
import com.rcpl.platform.communication.CommunicationDtos.ThreadDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Communication threads API. */
@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    private final CommunicationService communicationService;

    public CommunicationController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @GetMapping("/threads")
    @RequireScreen("/communication")
    public List<ThreadDto> threads() {
        return communicationService.listThreads();
    }

    @GetMapping("/threads/{code}")
    @RequireScreen("/communication")
    public ThreadDto thread(@PathVariable String code) {
        return communicationService.getThread(code);
    }

    /** Open (create if needed) a case-discussion thread. */
    @PostMapping("/threads")
    @RequireScreen(value = "/communication", manage = true)
    public ThreadDto ensure(@Valid @RequestBody EnsureThreadRequest req) {
        return communicationService.ensureThread(req.code(), req.town(), req.partnerName(), req.audience());
    }

    @PostMapping("/threads/{code}/messages")
    @RequireScreen(value = "/communication", manage = true)
    public ThreadDto post(@PathVariable String code, @Valid @RequestBody PostMessageRequest req) {
        return communicationService.postMessage(code, req);
    }

    @PostMapping("/threads/{code}/nudge")
    @RequireScreen(value = "/communication", manage = true)
    public ThreadDto nudge(@AuthenticationPrincipal CurrentUser user, @PathVariable String code,
                           @Valid @RequestBody NudgeRequest req) {
        return communicationService.nudge(user, code, req);
    }

    @PostMapping("/threads/{code}/request-info")
    @RequireScreen(value = "/communication", manage = true)
    public ThreadDto requestInfo(@PathVariable String code, @Valid @RequestBody RequestInfoRequest req) {
        return communicationService.requestInfo(code, req);
    }
}
