package com.graso.anitrack.administrator.service;

import com.graso.anitrack.administrator.controller.dto.AdminUserResponse;
import com.graso.anitrack.administrator.model.AdminUser;
import com.graso.anitrack.administrator.repository.AdminUserRepository;
import com.graso.anitrack.config.ForbiddenException;
import com.graso.anitrack.user.client.myanimelist.MyAnimeListUserClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;
    private final MyAnimeListUserClient myAnimeListUserClient;
    private final String rootUsername;

    public AdminUserService(AdminUserRepository adminUserRepository,
                            MyAnimeListUserClient myAnimeListUserClient,
                            @Value("${anitrack.admin.root-username}") String rootUsername) {
        this.adminUserRepository = adminUserRepository;
        this.myAnimeListUserClient = myAnimeListUserClient;
        this.rootUsername = rootUsername;
    }

    public boolean isRoot(String username) {
        return username != null && username.equalsIgnoreCase(rootUsername);
    }

    public boolean isAdmin(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return isRoot(username) || adminUserRepository.existsById(username);
    }

    @Cacheable(value = "resolvedUserCache", key = "#token")
    public String resolveUsername(String token) {
        return myAnimeListUserClient.getMyUser(token).getName();
    }

    public void requireAdmin(String token) {
        if (token == null || token.isBlank()) {
            throw new ForbiddenException("Se requiere autenticación para acceder al panel de administración");
        }
        if (!isAdmin(resolveUsername(token))) {
            throw new ForbiddenException("Se requiere rol de administrador");
        }
    }

    public List<AdminUserResponse> listAdmins() {
        List<AdminUserResponse> admins = new ArrayList<>();
        admins.add(new AdminUserResponse(rootUsername, null, true));
        adminUserRepository.findAll().forEach(admin ->
                admins.add(new AdminUserResponse(admin.getMalUsername(), admin.getCreatedAt(), false)));
        return admins;
    }

    public void addAdmin(String username) {
        String normalized = username.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        if (isRoot(normalized) || adminUserRepository.existsById(normalized)) {
            return;
        }
        adminUserRepository.save(new AdminUser(normalized, LocalDateTime.now()));
    }

    public void removeAdmin(String username) {
        if (isRoot(username)) {
            throw new IllegalArgumentException("No se puede eliminar al administrador raíz");
        }
        adminUserRepository.deleteById(username);
    }
}
