package com.rcpl.platform.settings;

import com.rcpl.platform.auth.CurrentUser;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Per-user settings API (My Settings) — each user reads/updates only their own. */
@RestController
@RequestMapping("/api/me/settings")
public class UserSettingsController {

    private final UserSettingsRepository repository;

    public UserSettingsController(UserSettingsRepository repository) {
        this.repository = repository;
    }

    public record UserSettingsDto(String inboxProvider, String inboxAddress,
                                  boolean autoForwardUnmatched, int slaHours) {
        static UserSettingsDto from(UserSettings s) {
            return new UserSettingsDto(s.getInboxProvider(), s.getInboxAddress(),
                    s.isAutoForwardUnmatched(), s.getSlaHours());
        }
    }

    public record UpdateSettingsRequest(
            String inboxProvider, String inboxAddress, Boolean autoForwardUnmatched,
            @Min(1) @Max(168) Integer slaHours) {
    }

    @GetMapping
    @Transactional(readOnly = true)
    public UserSettingsDto get(@AuthenticationPrincipal CurrentUser user) {
        return UserSettingsDto.from(repository.findById(user.id()).orElseGet(() -> defaults(user.id())));
    }

    @PutMapping
    @Transactional
    public UserSettingsDto update(@AuthenticationPrincipal CurrentUser user,
                                  @RequestBody UpdateSettingsRequest req) {
        UserSettings s = repository.findById(user.id()).orElseGet(() -> defaults(user.id()));
        if (req.inboxProvider() != null) s.setInboxProvider(req.inboxProvider().isBlank() ? null : req.inboxProvider());
        if (req.inboxAddress() != null) s.setInboxAddress(req.inboxAddress());
        if (req.autoForwardUnmatched() != null) s.setAutoForwardUnmatched(req.autoForwardUnmatched());
        if (req.slaHours() != null) s.setSlaHours(req.slaHours());
        return UserSettingsDto.from(repository.save(s));
    }

    private UserSettings defaults(String userId) {
        UserSettings s = new UserSettings();
        s.setUserId(userId);
        s.setAutoForwardUnmatched(true);
        s.setSlaHours(24);
        return s;
    }
}
