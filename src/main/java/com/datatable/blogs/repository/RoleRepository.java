package com.datatable.blogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datatable.blogs.modal.Role;
import com.datatable.blogs.modal.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}

