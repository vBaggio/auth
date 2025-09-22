package com.auth.config;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }
    
    private void initializeRoles() {
        if (roleRepository.findByName(Role.RoleName.ADMIN).isEmpty()) {
            Role adminRole = new Role(Role.RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
        
        if (roleRepository.findByName(Role.RoleName.DEFAULT).isEmpty()) {
            Role defaultRole = new Role(Role.RoleName.DEFAULT);
            roleRepository.save(defaultRole);
        }
    }
    
    private void initializeAdminUser() {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail("admin@admin.com");
            adminUser.setPassword(passwordEncoder.encode("S&cr3T#120"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            adminUser.setRoles(Set.of(adminRole));
            
            userRepository.save(adminUser);
            System.out.println("Admin user created: admin@admin.com");
        }
    }
}
