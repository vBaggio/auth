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

/**
 * Testes unitários para AuthService
 * 
 * Conceitos aplicados:
 * - @ExtendWith(MockitoExtension.class): Habilita o Mockito para esta classe de teste
 * - @Mock: Cria mocks das dependências
 * - @InjectMocks: Injeta os mocks no objeto sendo testado
 * - AAA Pattern: Arrange, Act, Assert
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    // MOCKS - Dependências que serão "falsificadas"
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

    // OBJETO EM TESTE - AuthService com as dependências mockadas injetadas
    @InjectMocks
    private AuthService authService;

    // DADOS DE TESTE - Objetos que usaremos nos testes
    private User testUser;
    private Role defaultRole;
    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;

    /**
     * Setup executado antes de cada teste
     * Aqui criamos os dados de teste que serão reutilizados
     */
    @BeforeEach
    void setUp() {
        // Criando role padrão
        defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setName(Role.RoleName.DEFAULT);

        // Criando usuário de teste
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("João");
        testUser.setLastName("Silva");
        testUser.setEmail("joao@email.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRoles(Set.of(defaultRole));

        // Criando DTOs de teste
        registerDTO = new RegisterDTO("joao@email.com", "senha123", "João", "Silva");
        loginDTO = new LoginDTO("joao@email.com", "senha123");
    }

    /**
     * TESTE 1: Registro de usuário com sucesso
     * 
     * Cenário: Usuário se registra pela primeira vez
     * Resultado esperado: Usuário é criado e token JWT é gerado
     */
    @Test
    @DisplayName("Deve registrar usuário com sucesso quando email não existe")
    void deveRegistrarUsuarioComSucesso() {
        // ARRANGE - Preparando os dados e comportamentos dos mocks
        String token = "jwt-token-123";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000); // 1 hora
        LocalDateTime expiresAt = expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        // Configurando o comportamento dos mocks
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false); // Email não existe
        when(roleRepository.findByName(Role.RoleName.DEFAULT)).thenReturn(Optional.of(defaultRole)); // Role existe
        when(passwordEncoder.encode("senha123")).thenReturn("encodedPassword123"); // Senha é codificada
        when(userRepository.save(any(User.class))).thenReturn(testUser); // Usuário é salvo
        when(jwtService.generateToken(any(User.class))).thenReturn(token); // Token é gerado
        when(jwtService.getExpirationDate()).thenReturn(expirationDate); // Data de expiração

        // ACT - Executando o método que queremos testar
        AuthDTO result = authService.register(registerDTO);

        // ASSERT - Verificando se o resultado está correto
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.firstName()).isEqualTo("João");
        assertThat(result.lastName()).isEqualTo("Silva");
        assertThat(result.email()).isEqualTo("joao@email.com");
        assertThat(result.expiresAt()).isEqualTo(expiresAt);

        // VERIFY - Verificando se os métodos dos mocks foram chamados corretamente
        verify(userRepository).existsByEmail("joao@email.com");
        verify(roleRepository).findByName(Role.RoleName.DEFAULT);
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    /**
     * TESTE 2: Falha no registro por email duplicado
     * 
     * Cenário: Usuário tenta se registrar com email que já existe
     * Resultado esperado: Exceção EmailAlreadyExistsException é lançada
     */
    @Test
    @DisplayName("Deve lançar exceção quando email já existe no registro")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // ARRANGE
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true); // Email já existe

        // ACT & ASSERT - Executando e verificando se a exceção é lançada
        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Este email já está em uso");

        // VERIFY - Verificando que não chegou até salvar o usuário
        verify(userRepository).existsByEmail("joao@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * TESTE 3: Falha no registro por role não encontrada
     * 
     * Cenário: Role padrão não existe no banco
     * Resultado esperado: Exceção RoleNotFoundException é lançada
     */
    @Test
    @DisplayName("Deve lançar exceção quando role padrão não é encontrada")
    void deveLancarExcecaoQuandoRolePadraoNaoExiste() {
        // ARRANGE
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.DEFAULT)).thenReturn(Optional.empty()); // Role não existe

        // ACT & ASSERT
        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Função padrão não encontrada");

        // VERIFY
        verify(userRepository).existsByEmail("joao@email.com");
        verify(roleRepository).findByName(Role.RoleName.DEFAULT);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * TESTE 4: Login com sucesso
     * 
     * Cenário: Usuário faz login com credenciais válidas
     * Resultado esperado: Token JWT é gerado e retornado
     */
    @Test
    @DisplayName("Deve fazer login com sucesso quando credenciais são válidas")
    void deveFazerLoginComSucesso() {
        // ARRANGE
        String token = "jwt-token-123";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        LocalDateTime expiresAt = expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(token);
        when(jwtService.getExpirationDate()).thenReturn(expirationDate);

        // ACT
        AuthDTO result = authService.login(loginDTO);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.email()).isEqualTo("joao@email.com");
        assertThat(result.firstName()).isEqualTo("João");
        assertThat(result.lastName()).isEqualTo("Silva");
        assertThat(result.expiresAt()).isEqualTo(expiresAt);

        // VERIFY
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(testUser);
    }

    /**
     * TESTE 5: Falha no login por credenciais inválidas
     * 
     * Cenário: Usuário tenta fazer login com senha incorreta
     * Resultado esperado: Exceção InvalidCredentialsException é lançada
     */
    @Test
    @DisplayName("Deve lançar exceção quando credenciais são inválidas")
    void deveLancarExcecaoQuandoCredenciaisSaoInvalidas() {
        // ARRANGE
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // ACT & ASSERT
        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        // VERIFY
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    /**
     * TESTE 6: Obter usuário atual
     * 
     * Cenário: Verificar se consegue obter o usuário logado
     * Resultado esperado: Retorna o usuário do contexto de segurança
     */
    @Test
    @DisplayName("Deve retornar usuário atual quando autenticado")
    void deveRetornarUsuarioAtualQuandoAutenticado() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        // ACT
        User result = authService.getCurrentUser();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
    }

    /**
     * TESTE 7: Obter usuário atual quando não autenticado
     * 
     * Cenário: Tentar obter usuário quando não há autenticação
     * Resultado esperado: Retorna null
     */
    @Test
    @DisplayName("Deve retornar null quando usuário não está autenticado")
    void deveRetornarNullQuandoUsuarioNaoAutenticado() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // ACT
        User result = authService.getCurrentUser();

        // ASSERT
        assertThat(result).isNull();
    }
}
