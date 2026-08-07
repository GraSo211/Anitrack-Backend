package com.graso.anitrack.administrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "admin_user")
public class AdminUser {

    @Id
    @Column(name = "mal_username", length = 64)
    private String malUsername;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AdminUser() {
    }

    public AdminUser(String malUsername, LocalDateTime createdAt) {
        this.malUsername = malUsername;
        this.createdAt = createdAt;
    }
}
