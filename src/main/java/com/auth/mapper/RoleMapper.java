package com.auth.mapper;

import com.auth.dto.RoleDTO;
import com.auth.entity.Role;

public class RoleMapper {

    public static RoleDTO toDto(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleDTO(role.getName().name());
    }
}
