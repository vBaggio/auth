package com.auth.service;

import com.auth.dto.AuthDTO;
import com.auth.dto.LoginDTO;
import com.auth.dto.RegisterDTO;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.EmailAlreadyExistsException;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.RoleNotFoundException;
import com.auth.mapper.AuthMapper;
import com.auth.mapper.UserMapper;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    
    @Transactional
    public AuthDTO register(RegisterDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Este email já está em uso");
        }
        
        // Create new user using MapStruct
        User user = UserMapper.INSTANCE.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        
        // Set default role
        Role defaultRole = roleRepository.findByName(Role.RoleName.DEFAULT)
                .orElseThrow(() -> new RoleNotFoundException("Função padrão não encontrada"));
        user.setRoles(Set.of(defaultRole));
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtService.generateToken(savedUser);
        Date expirationDate = jwtService.getExpirationDate();
        LocalDateTime expiresAt = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        return AuthMapper.toAuthDTO(savedUser, token, expiresAt);
    }
    
    @Transactional(readOnly = true)
    public AuthDTO login(LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);
            Date expirationDate = jwtService.getExpirationDate();
            LocalDateTime expiresAt = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            
            return AuthMapper.toAuthDTO(user, token, expiresAt);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
