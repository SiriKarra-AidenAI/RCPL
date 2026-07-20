package com.rcpl.platform.user;

import java.util.List;

import com.rcpl.platform.auth.PermissionService;
import com.rcpl.platform.auth.dto.UserProfileDto;
import com.rcpl.platform.auth.entity.UserEntity;
import com.rcpl.platform.auth.repository.RoleRepository;
import com.rcpl.platform.auth.repository.UserRepository;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.Ids;
import com.rcpl.platform.user.UserDtos.CreateUserRequest;
import com.rcpl.platform.user.UserDtos.UpdateUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Admin user directory management. */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
    }

    @Transactional(readOnly = true)
    public List<UserProfileDto> list() {
        return userRepository.findAll().stream().map(permissionService::toProfile).toList();
    }

    @Transactional
    public UserProfileDto create(CreateUserRequest req) {
        requireRole(req.roleCode());
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException.Conflict("email_taken", "A user with this email already exists");
        }
        UserEntity u = new UserEntity();
        u.setId(Ids.newId("u"));
        u.setName(req.name());
        u.setEmail(req.email());
        u.setRoleCode(req.roleCode());
        u.setRegion(req.region());
        u.setState(req.state());
        u.setActive(req.isActive() == null || req.isActive());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        return permissionService.toProfile(userRepository.save(u));
    }

    @Transactional
    public UserProfileDto update(String id, UpdateUserRequest req) {
        UserEntity u = userRepository.findById(id)
                .orElseThrow(() -> new ApiException.NotFound("User not found: " + id));
        if (req.name() != null) u.setName(req.name());
        if (req.email() != null) u.setEmail(req.email());
        if (req.roleCode() != null) {
            requireRole(req.roleCode());
            u.setRoleCode(req.roleCode());
        }
        if (req.region() != null) u.setRegion(req.region());
        if (req.state() != null) u.setState(req.state());
        if (req.isActive() != null) u.setActive(req.isActive());
        if (req.password() != null && !req.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        }
        return permissionService.toProfile(userRepository.save(u));
    }

    @Transactional
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException.NotFound("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private void requireRole(String roleCode) {
        if (!roleRepository.existsById(roleCode)) {
            throw new ApiException.BadRequest("Unknown role: " + roleCode);
        }
    }
}
