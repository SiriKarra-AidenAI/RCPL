package com.rcpl.platform.auth;

import com.rcpl.platform.auth.dto.AuthDtos.LoginRequest;
import com.rcpl.platform.auth.dto.AuthDtos.MeResponse;
import com.rcpl.platform.auth.dto.AuthDtos.TokenResponse;
import com.rcpl.platform.auth.dto.ScreenPermissionDto;
import com.rcpl.platform.auth.entity.UserEntity;
import com.rcpl.platform.auth.repository.UserRepository;
import com.rcpl.platform.common.ApiException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Authentication endpoints: login, current profile, per-screen permission check. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PermissionService permissionService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.permissionService = permissionService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest body) {
        UserEntity user = userRepository.findByEmailIgnoreCase(body.email()).orElse(null);
        // Identical error for unknown email and wrong password — don't leak which emails exist.
        if (user == null || !user.isActive() || !passwordEncoder.matches(body.password(), user.getPasswordHash())) {
            throw new ApiException(org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "invalid_credentials", "Invalid email or password");
        }
        return new TokenResponse(jwtService.issue(user), permissionService.toProfile(user));
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal CurrentUser current) {
        UserEntity user = userRepository.findById(current.id())
                .orElseThrow(() -> new ApiException.NotFound("User not found"));
        return new MeResponse(permissionService.toProfile(user));
    }

    @GetMapping("/can/{screenPath}")
    public ScreenPermissionDto can(@AuthenticationPrincipal CurrentUser current,
                                   @PathVariable String screenPath) {
        String path = screenPath.startsWith("/") ? screenPath : "/" + screenPath;
        return permissionService.can(current.roleCode(), path);
    }
}
