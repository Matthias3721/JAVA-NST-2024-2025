package org.example.medmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medmanagement.dto.PrescriptionDto;
import org.example.medmanagement.security.JwtAuthenticationFilter;
import org.example.medmanagement.security.JwtUtil;
import org.example.medmanagement.service.PrescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testy kontrolera PrescriptionController.
 */
@WebMvcTest(
        controllers = PrescriptionController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrescriptionService prescriptionService;

    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private PrescriptionDto presc1;
    private PrescriptionDto presc2;

    @BeforeEach
    void setUp() {
        presc1 = new PrescriptionDto();
        presc1.setId(1L);
        presc1.setUserId(100L);
        presc1.setMedicationId(10L);
        presc1.setDose("10mg");
        presc1.setFrequency("2x dziennie");
        presc1.setStartDate(LocalDate.of(2025, 1, 15));
        presc1.setEndDate(LocalDate.of(2025, 2, 15));

        presc2 = new PrescriptionDto();
        presc2.setId(2L);
        presc2.setUserId(200L);
        presc2.setMedicationId(20L);
        presc2.setDose("20mg");
        presc2.setFrequency("1x dziennie");
        presc2.setStartDate(LocalDate.of(2025, 2, 10));
        presc2.setEndDate(LocalDate.of(2025, 3, 10));
    }

    // --------------------------------------
    // 1) POST /api/prescriptions → create(...)
    // --------------------------------------
    @Test
    @DisplayName("POST /api/prescriptions → 201 Created zwraca utworzony PrescriptionDto")
    void create_ShouldReturnCreatedDto() throws Exception {
        PrescriptionDto in = new PrescriptionDto();
        in.setUserId(100L);
        in.setMedicationId(10L);
        in.setDose("10mg");
        in.setFrequency("2x dziennie");
        in.setStartDate(LocalDate.of(2025, 1, 15));
        in.setEndDate(LocalDate.of(2025, 2, 15));

        when(prescriptionService.create(ArgumentMatchers.any(PrescriptionDto.class))).thenReturn(presc1);

        mockMvc.perform(post("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(100)))
                .andExpect(jsonPath("$.dose", is("10mg")))
                .andExpect(jsonPath("$.frequency", is("2x dziennie")))
                .andExpect(jsonPath("$.startDate", is("2025-01-15")))
                .andExpect(jsonPath("$.endDate", is("2025-02-15")));

        verify(prescriptionService, times(1)).create(ArgumentMatchers.any(PrescriptionDto.class));
        verifyNoMoreInteractions(prescriptionService);
    }

    @Test
    @DisplayName("POST /api/prescriptions → 400 Bad Request, gdy JSON niepoprawny")
    void create_ShouldReturnBadRequest_WhenInvalidJson() throws Exception {
        // Jeśli body nie jest parsowalnym JSON-em, Spring zwróci 400 Bad Request
        mockMvc.perform(post("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-a-json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(prescriptionService);
    }

    @Test
    @DisplayName("POST /api/prescriptions → gdy serwis rzuca wyjątek, spodziewamy się ServletException")
    void create_ShouldThrowException_WhenServiceThrows() {
        PrescriptionDto in = new PrescriptionDto();
        in.setUserId(100L);
        in.setMedicationId(10L);
        in.setDose("10mg");
        in.setFrequency("2x dziennie");
        in.setStartDate(LocalDate.of(2025, 1, 15));
        in.setEndDate(LocalDate.of(2025, 2, 15));

        when(prescriptionService.create(ArgumentMatchers.any(PrescriptionDto.class)))
                .thenThrow(new RuntimeException("Błąd przy tworzeniu"));

        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/api/prescriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(in)))
                    .andReturn();
        });

        verify(prescriptionService, times(1)).create(ArgumentMatchers.any(PrescriptionDto.class));
        verifyNoMoreInteractions(prescriptionService);
    }

    // --------------------------------------
    // 2) GET /api/prescriptions/{id} → getById(...)
    // --------------------------------------
    @Test
    @DisplayName("GET /api/prescriptions/1 → 200 OK z istniejącym PrescriptionDto")
    void getById_ShouldReturnDto_WhenExists() throws Exception {
        when(prescriptionService.findById(1L)).thenReturn(presc1);

        mockMvc.perform(get("/api/prescriptions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.medicationId", is(10)))
                .andExpect(jsonPath("$.dose", is("10mg")))
                .andExpect(jsonPath("$.startDate", is("2025-01-15")));

        verify(prescriptionService, times(1)).findById(1L);
        verifyNoMoreInteractions(prescriptionService);
    }

    @Test
    @DisplayName("GET /api/prescriptions/99 → 404 Not Found, gdy serwis rzuca wyjątek")
    void getById_ShouldReturnNotFound_WhenServiceThrows() throws Exception {
        when(prescriptionService.findById(99L))
                .thenThrow(new RuntimeException("Recepta nie znaleziona: id=99"));

        mockMvc.perform(get("/api/prescriptions/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Recepta nie znaleziona: id=99")));

        verify(prescriptionService, times(1)).findById(99L);
        verifyNoMoreInteractions(prescriptionService);
    }

    // --------------------------------------
    // 3) PUT /api/prescriptions/{id} → update(...)
    // --------------------------------------
    @Test
    @DisplayName("PUT /api/prescriptions/1 → 200 OK z zaktualizowanym PrescriptionDto")
    void update_ShouldReturnUpdatedDto() throws Exception {
        PrescriptionDto in = new PrescriptionDto();
        in.setUserId(100L);
        in.setMedicationId(10L);
        in.setDose("15mg");
        in.setFrequency("3x dziennie");
        in.setStartDate(LocalDate.of(2025, 1, 20));
        in.setEndDate(LocalDate.of(2025, 2, 20));

        PrescriptionDto updated = new PrescriptionDto();
        updated.setId(1L);
        updated.setUserId(100L);
        updated.setMedicationId(10L);
        updated.setDose("15mg");
        updated.setFrequency("3x dziennie");
        updated.setStartDate(LocalDate.of(2025, 1, 20));
        updated.setEndDate(LocalDate.of(2025, 2, 20));

        when(prescriptionService.update(eq(1L), ArgumentMatchers.any(PrescriptionDto.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/prescriptions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.dose", is("15mg")))
                .andExpect(jsonPath("$.frequency", is("3x dziennie")))
                .andExpect(jsonPath("$.startDate", is("2025-01-20")))
                .andExpect(jsonPath("$.endDate", is("2025-02-20")));

        verify(prescriptionService, times(1))
                .update(eq(1L), ArgumentMatchers.any(PrescriptionDto.class));
        verifyNoMoreInteractions(prescriptionService);
    }

    @Test
    @DisplayName("PUT /api/prescriptions/1 → 404 Not Found, gdy serwis rzuca wyjątek")
    void update_ShouldReturnNotFound_WhenServiceThrows() throws Exception {
        PrescriptionDto in = new PrescriptionDto();
        in.setUserId(100L);
        in.setMedicationId(10L);
        in.setDose("15mg");
        in.setFrequency("3x dziennie");
        in.setStartDate(LocalDate.of(2025, 1, 20));
        in.setEndDate(LocalDate.of(2025, 2, 20));

        when(prescriptionService.update(eq(1L), ArgumentMatchers.any(PrescriptionDto.class)))
                .thenThrow(new RuntimeException("Recepta nie znaleziona: id=1"));

        mockMvc.perform(put("/api/prescriptions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Recepta nie znaleziona: id=1")));

        verify(prescriptionService, times(1))
                .update(eq(1L), ArgumentMatchers.any(PrescriptionDto.class));
        verifyNoMoreInteractions(prescriptionService);
    }

    // --------------------------------------
    // 4) DELETE /api/prescriptions/{id} → delete(...)
    // --------------------------------------
    @Test
    @DisplayName("DELETE /api/prescriptions/1 → 204 No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(prescriptionService).delete(1L);

        mockMvc.perform(delete("/api/prescriptions/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(prescriptionService, times(1)).delete(1L);
        verifyNoMoreInteractions(prescriptionService);
    }

    @Test
    @DisplayName("DELETE /api/prescriptions/99 → 404 Not Found, gdy serwis rzuca wyjątek")
    void delete_ShouldReturnNotFound_WhenServiceThrows() throws Exception {
        doThrow(new RuntimeException("Recepta nie znaleziona: id=99"))
                .when(prescriptionService).delete(99L);

        mockMvc.perform(delete("/api/prescriptions/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Recepta nie znaleziona: id=99")));

        verify(prescriptionService, times(1)).delete(99L);
        verifyNoMoreInteractions(prescriptionService);
    }

    // --------------------------------------
    // 5) GET /api/prescriptions → findAll()
    // --------------------------------------
    @Test
    @DisplayName("GET /api/prescriptions → 200 OK z listą wszystkich PrescriptionDto")
    void findAll_ShouldReturnList() throws Exception {
        when(prescriptionService.findAll()).thenReturn(List.of(presc1, presc2));

        mockMvc.perform(get("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].userId", is(100)))
                .andExpect(jsonPath("$[1].medicationId", is(20)));

        verify(prescriptionService, times(1)).findAll();
        verifyNoMoreInteractions(prescriptionService);
    }

    // --------------------------------------
    // 6) GET /api/prescriptions/search?userId=... → findByUserId()
    // --------------------------------------
    @Test
    @DisplayName("GET /api/prescriptions/search?userId=100 → 200 OK z listą PrescriptionDto")
    void searchByUserId_ShouldReturnMatchingList() throws Exception {
        when(prescriptionService.findByUserId(100L)).thenReturn(List.of(presc1));

        mockMvc.perform(get("/api/prescriptions/search")
                        .param("userId", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].dose", is("10mg")));

        verify(prescriptionService, times(1)).findByUserId(100L);
        verifyNoMoreInteractions(prescriptionService);
    }

    @Test
    @DisplayName("GET /api/prescriptions/search bez parametru userId → 400 Bad Request")
    void searchByUserId_ShouldReturnBadRequest_WhenMissingParam() throws Exception {
        mockMvc.perform(get("/api/prescriptions/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(prescriptionService);
    }
}
