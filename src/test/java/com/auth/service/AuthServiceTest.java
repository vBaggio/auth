package com.auth.service;

import com.auth.dto.AuthDTO;
import com.auth.dto.LoginDTO;
import com.auth.dto.RegisterDTO;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.EmailAlreadyExistsException;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.RoleNotFoundException;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role defaultRole;
    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setName(Role.RoleName.DEFAULT);
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("João");
        testUser.setLastName("Silva");
        testUser.setEmail("joao@email.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRoles(Set.of(defaultRole));

        registerDTO = new RegisterDTO("joao@email.com", "senha123", "João", "Silva");
        loginDTO = new LoginDTO("joao@email.com", "senha123");
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso quando email não existe")
    void deveRegistrarUsuarioComSucesso() {
        String token = "jwt-token-123";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        LocalDateTime expiresAt = expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.DEFAULT)).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode("senha123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);
        when(jwtService.getExpirationDate()).thenReturn(expirationDate);

        AuthDTO result = authService.register(registerDTO);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.firstName()).isEqualTo("João");
        assertThat(result.lastName()).isEqualTo("Silva");
        assertThat(result.email()).isEqualTo("joao@email.com");
        assertThat(result.expiresAt()).isEqualTo(expiresAt);
        verify(userRepository).existsByEmail("joao@email.com");
        verify(roleRepository).findByName(Role.RoleName.DEFAULT);
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe no registro")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Este email já está em uso");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando role padrão não é encontrada")
    void deveLancarExcecaoQuandoRolePadraoNaoExiste() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.DEFAULT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Função padrão não encontrada");

        verify(userRepository).existsByEmail("joao@email.com");
        verify(roleRepository).findByName(Role.RoleName.DEFAULT);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve fazer login com sucesso quando credenciais são válidas")
    void deveFazerLoginComSucesso() {
        String token = "jwt-token-123";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        LocalDateTime expiresAt = expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(token);
        when(jwtService.getExpirationDate()).thenReturn(expirationDate);

        AuthDTO result = authService.login(loginDTO);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.email()).isEqualTo("joao@email.com");
        assertThat(result.firstName()).isEqualTo("João");
        assertThat(result.lastName()).isEqualTo("Silva");
        assertThat(result.expiresAt()).isEqualTo(expiresAt);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(testUser);
    }

    @Test
    @DisplayName("Deve lançar exceção quando credenciais são inválidas")
    void deveLancarExcecaoQuandoCredenciaisSaoInvalidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar usuário atual quando autenticado")
    void deveRetornarUsuarioAtualQuandoAutenticado() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        User result = authService.getCurrentUser();

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar null quando usuário não está autenticado")
    void deveRetornarNullQuandoUsuarioNaoAutenticado() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        User result = authService.getCurrentUser();

        assertThat(result).isNull();
    }
}
