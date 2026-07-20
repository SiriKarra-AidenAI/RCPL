package com.rcpl.platform.auth;

import com.rcpl.platform.auth.entity.UserEntity;
import com.rcpl.platform.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Provisions a single bootstrap admin user on first startup if the users table is empty,
 * using a real BCrypt hash (which is why this isn't a plain SQL migration). This is the only
 * user created automatically — all others are created through the Admin screen. No demo data.
 */
@Component
public class BootstrapAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public BootstrapAdminRunner(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                @Value("${app.bootstrap.admin-email:admin@rcpl.in}") String adminEmail,
                                @Value("${app.bootstrap.admin-password:admin123}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }
        UserEntity admin = new UserEntity();
        admin.setId("u-admin");
        admin.setName("Platform Admin");
        admin.setEmail(adminEmail);
        admin.setRoleCode("admin");
        admin.setRegion("HQ");
        admin.setActive(true);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        userRepository.save(admin);
        log.info("Bootstrapped admin user '{}' (change the password immediately).", adminEmail);
    }
}
