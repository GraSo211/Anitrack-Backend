package com.graso.anitrack.administrator.repository;

import com.graso.anitrack.administrator.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, String> {

}
