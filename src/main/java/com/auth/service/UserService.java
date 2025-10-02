package com.auth.service;

import com.auth.dto.RoleDTO;
import com.auth.dto.UserDTO;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.RoleNotFoundException;
import com.auth.exception.UserNotFoundException;
import com.auth.mapper.UserMapper;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    
    private static final String USER_NOT_FOUND_BY_ID = "Usuário não encontrado com ID: ";
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllWithRoles().stream()
                .map(UserMapper.INSTANCE::toDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID + id));
        return UserMapper.INSTANCE.toDto(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com email: " + email));
        return UserMapper.INSTANCE.toDto(user);
    }

    @Transactional
    public UserDTO addRolesToUser(Long id, Set<RoleDTO> roles) {
        
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID + id));

        Set<Role.RoleName> roleNames = roles.stream().map(RoleDTO::name).map(Role.RoleName::from).collect(Collectors.toSet());

        Set<Role> foundRoles = roleRepository.findByNameIn(roleNames)
            .orElseThrow(() -> new RoleNotFoundException("Uma ou mais roles não encontradas"));

        if (foundRoles.isEmpty() || foundRoles.size() != roleNames.size()) {
            throw new RoleNotFoundException("Uma ou mais roles não encontradas");
        }

        for (Role role : foundRoles) {
            if (user.getRoles().contains(role)) {
                throw new IllegalArgumentException("O usuário já possui a role: " + role.getName());
            }
        }
        
        user.getRoles().addAll(foundRoles);
        
        user = userRepository.save(user);
        
        return UserMapper.INSTANCE.toDto(user);
    }

    @Transactional
    public UserDTO removeRolesFromUser(Long id, Set<RoleDTO> roles) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID + id));

        Set<Role.RoleName> roleNames = roles.stream()
                .map(RoleDTO::name)
                .map(Role.RoleName::from)
                .collect(Collectors.toSet());

        Set<Role> foundRoles = roleRepository.findByNameIn(roleNames)
                .orElseThrow(() -> new RoleNotFoundException("Uma ou mais roles não encontradas"));

        if (foundRoles.isEmpty() || foundRoles.size() != roleNames.size()) {
            throw new RoleNotFoundException("Uma ou mais roles não encontradas");
        }

        for (Role role : foundRoles) {
            if (!user.getRoles().contains(role)) {
                throw new IllegalArgumentException("O usuário não possui a role: " + role.getName());
            }
        }

        if (user.getRoles().size() - foundRoles.size() < 1) {
            throw new IllegalArgumentException("O usuário deve permanecer com pelo menos uma role");
        }

        user.getRoles().removeAll(foundRoles);

        user = userRepository.save(user);

        return UserMapper.INSTANCE.toDto(user);
    }
    
}
