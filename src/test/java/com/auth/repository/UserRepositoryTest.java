package com.auth.repository;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.auth.config.NoFlywayTestConfig;
import com.auth.entity.Role;
import com.auth.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository - Testes Unitários")
@Import(NoFlywayTestConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private RoleRepository roleRepository;

    @Test
    void testExistsByEmail() {
        userRepository.save(new User("test@example.com", "password", "First", "Last"));
        Assertions.assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    void testfindAllWithRoles() {
        Role adminRole = new Role(Role.RoleName.ADMIN);
        Role defaultRole = new Role(Role.RoleName.DEFAULT);
        
        adminRole = roleRepository.save(adminRole);
        defaultRole = roleRepository.save(defaultRole);

        User user1 = new User("adminuser@example.com", "password", "Role", "User");
        user1.addRole(adminRole);
        userRepository.save(user1);

        User user2 = new User("defaultuser@example.com", "password2", "Role2", "User2");
        user2.addRole(defaultRole);
        userRepository.save(user2);

        List<User> users = userRepository.findAllWithRoles();

        Assertions.assertThat(users).isNotNull().isNotEmpty().hasSize(2);

        User foundAdmin = users.stream()
            .filter(u -> u.getEmail().equals("adminuser@example.com"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("adminuser@example.com not found"));
        Assertions.assertThat(foundAdmin.getRoles()).isNotNull().isNotEmpty();
        Assertions.assertThat(foundAdmin.getRoles().stream().map(Role::getName)).contains(Role.RoleName.ADMIN);

        User foundDefault = users.stream()
            .filter(u -> u.getEmail().equals("defaultuser@example.com"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("defaultuser@example.com not found"));
        Assertions.assertThat(foundDefault.getRoles()).isNotNull().isNotEmpty();
        Assertions.assertThat(foundDefault.getRoles().stream().map(Role::getName)).contains(Role.RoleName.DEFAULT);
    }

    @Test
    void testFindByEmail() {
        var email = "test@example.com";
        userRepository.save(new User(email, "password", "First", "Last"));

        var foundUser = userRepository.findByEmail(email);

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getEmail()).isEqualTo(email);
    }

    @Test
    void testFindByIdWithRoles() {
        Role adminRole = roleRepository.save(new Role(Role.RoleName.ADMIN));
        Role defaultRole = roleRepository.save(new Role(Role.RoleName.DEFAULT));
        
        User user = new User("test@example.com", "password", "First", "Last");
        user.setRoles(Set.of(adminRole, defaultRole));
        user = userRepository.save(user);

        Assertions.assertThat(user.getId()).isNotNull();

        var foundUser = userRepository.findByIdWithRoles(user.getId());

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getId()).isEqualTo(user.getId());
        Assertions.assertThat(foundUser.get().getRoles()).isNotNull().isNotEmpty().hasSize(2);
        Assertions.assertThat(foundUser.get().getRoles().stream().map(Role::getName)).containsExactlyInAnyOrder(Role.RoleName.ADMIN, Role.RoleName.DEFAULT);
    }
}
