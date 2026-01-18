package org.example.qnrassignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.qnrassignment.core.enums.Role;
import org.example.qnrassignment.dto.AuthenticationDTO;
import org.example.qnrassignment.dto.LoginDTO;
import org.example.qnrassignment.dto.RegisterDTO;
import org.example.qnrassignment.model.User;
import org.example.qnrassignment.repository.UserRepository;
import org.example.qnrassignment.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;


    public AuthenticationDTO register(RegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role;
        try {
            role = Role.valueOf(registerDTO.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + registerDTO.getRole() + " must be ADMIN or USER");
        }

        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
        log.info("User {} has been registered", user.getUsername());

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return AuthenticationDTO.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(token)
                .message("Registration successful")
                .build();
    }

    public AuthenticationDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        log.info("User {} has logged in successfully.", user.getUsername());

        return AuthenticationDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .message("Login successful")
                .build();


    }

    public void logout(String token) {
        String username = jwtService.extractSubject(token);
        Date expirationDate = jwtService.extractExpiration(token);

        tokenBlacklistService.blacklistToken(token, username, expirationDate);

        log.info("User {} has been logged out. Token {} has been blacklisted", username, token);
    }
}
