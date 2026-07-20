package com.rcpl.platform.user;

import java.util.List;

import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.auth.dto.UserProfileDto;
import com.rcpl.platform.user.UserDtos.CreateUserRequest;
import com.rcpl.platform.user.UserDtos.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin user directory API (Settings > Team). */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequireScreen("/settings")
    public List<UserProfileDto> list() {
        return userService.list();
    }

    @PostMapping
    @RequireScreen(value = "/settings", manage = true)
    public UserProfileDto create(@Valid @RequestBody CreateUserRequest req) {
        return userService.create(req);
    }

    @PatchMapping("/{id}")
    @RequireScreen(value = "/settings", manage = true)
    public UserProfileDto update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest req) {
        return userService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @RequireScreen(value = "/settings", manage = true)
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }
}
