package com.auth.mapper;

import com.auth.dto.RegisterDTO;
import com.auth.dto.RoleDTO;
import com.auth.dto.UserDTO;
import com.auth.entity.Role;
import com.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserDTO toDto(User user);
    
    User toEntity(RegisterDTO registerDTO);
    
    default Set<RoleDTO> mapRolesToRoleDTO(Set<Role> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream().map(role -> new RoleDTO(role.getName().name())).collect(Collectors.toSet());
    }
    
}
