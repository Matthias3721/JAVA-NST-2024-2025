package org.example.medmanagement.controller;

import org.example.medmanagement.model.Medication;
import org.example.medmanagement.model.Role;
import org.example.medmanagement.model.User;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.repository.RoleRepository;
import org.example.medmanagement.repository.UserRepository;
import org.example.medmanagement.security.CustomUserDetailsService;
import org.example.medmanagement.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedicationControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;
    private Long medicationId;

    @BeforeEach
    void setUp() {
        // Czyścimy tabele, by testy startowały od „czystej” bazy
        medicationRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Tworzymy role
        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        roleRepository.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleRepository.save(roleAdmin);

        // Tworzymy użytkownika z rolą USER
        User user = new User();
        user.setEmail("normalUser@example.com");
        user.setPassword(passwordEncoder.encode("userPass123"));
        user.setRoles(new HashSet<>(Collections.singletonList(roleUser)));
        userRepository.save(user);

        // Tworzymy użytkownika z rolą ADMIN
        User admin = new User();
        admin.setEmail("adminUser@example.com");
        admin.setPassword(passwordEncoder.encode("adminPass123"));
        admin.setRoles(new HashSet<>(Collections.singletonList(roleAdmin)));
        userRepository.save(admin);

        // Generujemy tokeny dla obu użytkowników (JwtUtil przyjmuje String username, czyli email)
        userToken = jwtUtil.generateToken(user.getEmail());
        adminToken = jwtUtil.generateToken(admin.getEmail());

        // Dodajemy przykładowy lek do bazy, by można go było usunąć
        Medication med = new Medication();
        med.setName("TestowyLek");
        med.setSubstance("SubstancjaTestowa");
        med.setUnit("mg");
        med.setManufacturer("FirmaTest");
        Medication saved = medicationRepository.save(med);
        medicationId = saved.getId();
    }

    @Test
    void whenRoleUserDeletesMedication_thenForbidden() throws Exception {
        mockMvc.perform(delete("/api/medications/{id}", medicationId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenRoleAdminDeletesMedication_thenNoContent() throws Exception {
        mockMvc.perform(delete("/api/medications/{id}", medicationId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
