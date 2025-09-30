package com.auth.mapper;

import com.auth.dto.AuthDTO;
import com.auth.entity.User;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "type", constant = "Bearer")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "expiresAt", source = "expiresAt")
    AuthDTO toAuthDTO(User user, String token, LocalDateTime expiresAt);
}
