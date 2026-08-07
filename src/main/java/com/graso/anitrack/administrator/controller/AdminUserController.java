package com.graso.anitrack.administrator.controller;

import com.graso.anitrack.administrator.controller.dto.AdminUserRequest;
import com.graso.anitrack.administrator.controller.dto.AdminUserResponse;
import com.graso.anitrack.administrator.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAdminUsers(@CookieValue("access_token") String token) {
        adminUserService.requireAdmin(token);
        List<AdminUserResponse> admins = adminUserService.listAdmins();

        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addAdminUser(@CookieValue("access_token") String token,
                                             @Valid @RequestBody AdminUserRequest request) {
        adminUserService.requireAdmin(token);
        adminUserService.addAdmin(request.username());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> removeAdminUser(@CookieValue("access_token") String token,
                                                @PathVariable("username") String username) {
        adminUserService.requireAdmin(token);
        adminUserService.removeAdmin(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
