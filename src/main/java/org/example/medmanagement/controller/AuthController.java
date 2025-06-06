package org.example.medmanagement.controller;

import org.example.medmanagement.dto.AuthRequest;
import org.example.medmanagement.dto.RegisterRequest;
import org.example.medmanagement.dto.UserDto;
import org.example.medmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Kontroler odpowiedzialny za rejestrację i logowanie.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    /**
     * Logowanie: zwraca „dummy” token
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
            String token = "dummy-jwt-token-for-" + authRequest.getEmail();
            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Nieprawidłowy login lub hasło.");
        }
    }
}
