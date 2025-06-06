package org.example.medmanagement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe dla JwtAuthenticationFilter.
 */
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthorizationHeader_ShouldNotAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidToken_ShouldSetAuthentication() throws Exception {
        String token = "dummy.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Mock JwtUtil oraz userDetailsService:
        when(jwtUtil.getUsernameFromJwt(token)).thenReturn("testuser");
        when(jwtUtil.validateToken(token)).thenReturn(true);

        //fikcyjny UserDetails z jedną rolą
        var grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var userDetails = new org.springframework.security.core.userdetails.User(
                "testuser", "irrelevant", grantedAuthorities
        );
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        //filtr
        filter.doFilterInternal(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication powinno być ustawione");
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals("testuser", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
