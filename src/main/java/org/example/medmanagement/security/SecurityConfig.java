// Ścieżka: src/main/java/org/example/medmanagement/security/SecurityConfig.java
package org.example.medmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Konfiguracja Spring Security:
 *  - wszystkie ścieżki /api/auth/** dostępne publicznie
 *  - /swagger-ui/** i /v3/api-docs/** dostępne publicznie
 *  - /actuator/health dostępne publicznie
 *  - pozostałe endpointy (/api/medications/**, /api/stock/** itd.) wymagają uwierzytelnienia i/lub odpowiedniej roli
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguracja AuthenticationManager: podpinamy własny UserDetailsService
     * i PasswordEncoder, aby Spring umiał zweryfikować dane logowania.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1) Wyłączamy CSRF (używamy REST + tokenów)
        http.csrf(csrf -> csrf.disable());

        // 2) Sesja stateless – nie trzymamy sesji w pamięci (używamy JWT)
        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3) Reguły dostępu
        http.authorizeHttpRequests(auth -> auth
                // 3.1) Zezwól publicznie na rejestrację i logowanie
                .requestMatchers("/api/auth/**").permitAll()

                // 3.2) Zezwól publicznie na Swagger UI / OpenAPI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 3.3) Zezwól publicznie na Actuator Health
                .requestMatchers("/actuator/health").permitAll()

                // 3.4) /api/stock/** tylko dla ADMIN
                .requestMatchers("/api/stock/**").hasRole("ADMIN")

                // 3.5) /api/medications/**:
                .requestMatchers(HttpMethod.GET,    "/api/medications/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/medications/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/medications/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/medications/**").hasRole("ADMIN")


                .anyRequest().authenticated()
        );

        // 4) Podpinamy filtr JWT przed filtrem uwierzytelniania użytkownika
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 5) (Opcjonalnie) Wyłącz FrameOptions, jeśli korzystasz z H2 Console
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
