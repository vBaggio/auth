package com.auth.mapper;

import com.auth.dto.RoleDTO;
import com.auth.entity.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(target = "name", expression = "java(role.getName().name())")
    RoleDTO toDto(Role role);
}
