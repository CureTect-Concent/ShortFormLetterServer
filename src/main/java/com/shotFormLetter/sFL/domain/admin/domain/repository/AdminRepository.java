package com.shotFormLetter.sFL.domain.admin.domain.repository;

import com.shotFormLetter.sFL.domain.admin.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByAdminUserId(String AdminUserId);

    Admin findByAdminName(String AdminName);
}
