package com.auth.entity;

import jakarta.persistence.*;

import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
    
    public Role() {}
    
    public Role(RoleName name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RoleName getName() {
        return name;
    }
    
    public void setName(RoleName name) {
        this.name = name;
    }
    
    public Set<User> getUsers() {
        return users;
    }
    
    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    public enum RoleName {
        ADMIN, DEFAULT;

        public static RoleName from(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Role não pode ser nulo");
            }
            try {
                return RoleName.valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Role inválido: " + value);
            }
        }
    }
}
