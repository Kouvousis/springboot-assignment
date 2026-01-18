package org.example.qnrassignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.qnrassignment.dto.AuthenticationDTO;
import org.example.qnrassignment.dto.LoginDTO;
import org.example.qnrassignment.dto.LogoutDTO;
import org.example.qnrassignment.dto.RegisterDTO;
import org.example.qnrassignment.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("Registration request received for username: {}", registerDTO.getUsername());

        try {
            AuthenticationDTO authenticationDTO = authService.register(registerDTO);
            return new ResponseEntity<>(authenticationDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(AuthenticationDTO.builder()
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Login request received for username: {}", loginDTO.getUsername());

        try {
            AuthenticationDTO authenticationDTO = authService.login(loginDTO);
            return ResponseEntity.ok(authenticationDTO);
        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationDTO.builder()
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization") String authorizationHeader) {
        log.info("Logout request received");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Invalid Authorization header");
            return ResponseEntity.badRequest().body(LogoutDTO.builder()
                    .message("Invalid Authorization header")
                    .build()
                    .getMessage());
        }
        // Remove Bearer prefix
        String token = authorizationHeader.substring(7);
        try {
            authService.logout(token);
            return ResponseEntity.ok("Successfully logged out");
        } catch (IllegalArgumentException e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
