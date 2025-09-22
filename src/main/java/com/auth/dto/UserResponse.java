package com.auth.dto;

import com.auth.entity.Role;
import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role.RoleName> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public UserResponse() {}
    
    public UserResponse(Long id, String email, String firstName, String lastName, 
                       Set<Role.RoleName> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Set<Role.RoleName> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role.RoleName> roles) {
        this.roles = roles;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
