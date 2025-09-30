package com.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.auth.dto.RoleDTO;
import com.auth.dto.UserDTO;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.UserNotFoundException;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Unit Tests")
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    UserService userService;
    
    private User defaultUser;
    private User adminUser;

    private Role defaultRole;
    private Role adminRole;

    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String ADMIN_EMAIL = "admin@example.com";

    @BeforeEach
    void setUp() {
        defaultRole = new Role(Role.RoleName.DEFAULT);
        defaultUser = new User(DEFAULT_EMAIL, "password", "Test", "User");
        defaultUser.setId(1L);
        defaultUser.addRole(defaultRole);

        adminRole = new Role(Role.RoleName.ADMIN);
        adminUser = new User(ADMIN_EMAIL, "password", "Admin", "User");
        adminUser.setId(2L);
        adminUser.addRole(adminRole);
    }

    @Test
    @DisplayName("Should return UserDetails when loading user by existing username")
    void shouldReturnUserDetailsWhenLoadUserByExistingUsername() {
        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(defaultUser));
        
        UserDetails userDetails = userService.loadUserByUsername(DEFAULT_EMAIL);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_DEFAULT");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when loading user by non-existing username")
    void shouldThrowUsernameNotFoundExceptionWhenLoadUserByNonExistingUsername() {
        String nonExistingEmail = "nonexistent@example.com";
        
        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername(nonExistingEmail))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("Usuário não encontrado");
    
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        when(userRepository.findAllWithRoles()).thenReturn(List.of(defaultUser, adminUser));

        List<UserDTO> users = userService.getAllUsers();      
        assertThat(users).hasSize(2).isNotNull();
        assertThat(users.stream().map(UserDTO::email)).containsExactlyInAnyOrder(DEFAULT_EMAIL, ADMIN_EMAIL);
    assertThat(users.stream()
        .filter(u -> u.email().equals(DEFAULT_EMAIL))
        .findFirst()
        .orElseThrow()
        .roles())
        .extracting(RoleDTO::name)
        .containsExactly("DEFAULT");
    assertThat(users.stream()
        .filter(u -> u.email().equals(ADMIN_EMAIL))
        .findFirst()
        .orElseThrow()
        .roles())
        .extracting(RoleDTO::name)
        .containsExactly("ADMIN");
    }

    @Test
    @DisplayName("Should return user by ID when user exists")
    void shouldReturnUserByIdWhenUserExists() {
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(defaultUser));

        UserDTO userDTO = userService.getUserById(1L);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.id()).isEqualTo(1L);
        assertThat(userDTO.email()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDTO.roles()).hasSize(1);
        assertThat(userDTO.roles().iterator().next().name()).isEqualTo("DEFAULT");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when getting user by non-existing ID")
    void shouldThrowUserNotFoundExceptionWhenGettingUserByNonExistingId() {
        Long nonExistingId = 99L;
        when(userRepository.findByIdWithRoles(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(nonExistingId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Usuário não encontrado com ID: " + nonExistingId);
    }

    @Test
    @DisplayName("Should return user by email when user exists")
    void shouldReturnUserByEmailWhenUserExists() {
        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(defaultUser));

        UserDTO userDTO = userService.getUserByEmail(DEFAULT_EMAIL);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.email()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDTO.roles()).hasSize(1);
        assertThat(userDTO.roles().iterator().next().name()).isEqualTo("DEFAULT");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when getting user by non-existing email")
    void shouldThrowUserNotFoundExceptionWhenGettingUserByNonExistingEmail() {
        String nonExistingEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(nonExistingEmail))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("Usuário não encontrado com email: " + nonExistingEmail);
    }

    @Test
    @DisplayName("Should add roles to user successfully")
    void shouldAddRolesToUserSuccessfully() {
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(defaultUser));
        when(roleRepository.findByNameIn(Set.of(Role.RoleName.ADMIN))).thenReturn(Optional.of(Set.of(adminRole)));
        
        User userWithNewRole = new User(DEFAULT_EMAIL, "password", "Test", "User");
        userWithNewRole.setId(1L);
        userWithNewRole.addRole(defaultRole);
        userWithNewRole.addRole(adminRole);

        when(userRepository.save(defaultUser)).thenReturn(userWithNewRole);
        
        UserDTO updatedUserDTO = userService.addRolesToUser(1L, Set.of(new RoleDTO("ADMIN")));

        assertThat(updatedUserDTO).isNotNull();
        assertThat(updatedUserDTO.id()).isEqualTo(1L);
        assertThat(updatedUserDTO.roles()).hasSize(2);
        assertThat(updatedUserDTO.roles())
            .extracting(RoleDTO::name)
            .containsExactlyInAnyOrder("DEFAULT", "ADMIN");
    }
}