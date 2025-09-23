package com.auth.repository;

import com.auth.entity.Role;
import com.auth.entity.Role.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);

    Optional<Set<Role>> findByNameIn(Set<RoleName> roleNames);
}
