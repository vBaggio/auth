package com.auth.repository;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("UserRepository - Unit Tests")
@Import(NoFlywayTestConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private RoleRepository roleRepository;

    private Role adminRole;
    private Role defaultRole;
    private User testUser;

    private final String testEmail = "test@example.com";
    private final String adminEmail = "adminuser@example.com";
    private final String defaultEmail = "defaultuser@example.com";
    private final String password = "password";
    private final String firstName = "First";
    private final String lastName = "Last";

    @BeforeEach
    void setUp() {
        adminRole = roleRepository.save(new Role(Role.RoleName.ADMIN));
        defaultRole = roleRepository.save(new Role(Role.RoleName.DEFAULT));
        testUser = new User(testEmail, password, firstName, lastName);
    }

    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrueWhenEmailExists() {
        userRepository.save(testUser);
        Assertions.assertThat(userRepository.existsByEmail(testEmail)).isTrue();
    }

    @Test
    @DisplayName("Should retrieve all users with their roles")
    void shouldReturnAllUsersWithRoles() {
        User user1 = new User(adminEmail, password, "Role", "User");
        user1.addRole(adminRole);
        userRepository.save(user1);

        User user2 = new User(defaultEmail, "password2", "Role2", "User2");
        user2.addRole(defaultRole);
        userRepository.save(user2);

        List<User> users = userRepository.findAllWithRoles();

        Assertions.assertThat(users).isNotNull().isNotEmpty().hasSize(2);

        User foundAdmin = users.stream()
            .filter(u -> u.getEmail().equals(adminEmail))
            .findFirst()
            .orElseThrow(() -> new AssertionError("adminuser@example.com not found"));
        Assertions.assertThat(foundAdmin.getRoles()).isNotNull().isNotEmpty();
        Assertions.assertThat(foundAdmin.getRoles().stream().map(Role::getName)).contains(Role.RoleName.ADMIN);

        User foundDefault = users.stream()
            .filter(u -> u.getEmail().equals(defaultEmail))
            .findFirst()
            .orElseThrow(() -> new AssertionError("defaultuser@example.com not found"));
        Assertions.assertThat(foundDefault.getRoles()).isNotNull().isNotEmpty();
        Assertions.assertThat(foundDefault.getRoles().stream().map(Role::getName)).contains(Role.RoleName.DEFAULT);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldReturnUserByEmail() {
        userRepository.save(testUser);

        var foundUser = userRepository.findByEmail(testEmail);

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getEmail()).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("Should find user by id with their roles")
    void shouldReturnUserByIdWithRoles() {
        testUser.setRoles(Set.of(adminRole, defaultRole));
        User user = userRepository.save(testUser);

        Assertions.assertThat(user.getId()).isNotNull();

        var foundUser = userRepository.findByIdWithRoles(user.getId());

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getId()).isEqualTo(user.getId());
        Assertions.assertThat(foundUser.get().getRoles()).isNotNull().isNotEmpty().hasSize(2);
        Assertions.assertThat(foundUser.get().getRoles().stream().map(Role::getName)).containsExactlyInAnyOrder(Role.RoleName.ADMIN, Role.RoleName.DEFAULT);
    }
}
