package org.example.medmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medmanagement.dto.MedicationDto;
import org.example.medmanagement.security.JwtAuthenticationFilter;
import org.example.medmanagement.security.JwtUtil;
import org.example.medmanagement.service.MedicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testy kontrolera MedicationController.
 */
@WebMvcTest(
        controllers = MedicationController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationService medicationService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private MedicationDto medDto1;
    private MedicationDto medDto2;

    @BeforeEach
    void setUp() {
        medDto1 = new MedicationDto();
        medDto1.setId(1L);
        medDto1.setName("Paracetamol");
        medDto1.setSubstance("Paracetamol");
        medDto1.setUnit("mg");
        medDto1.setManufacturer("GSK");

        medDto2 = new MedicationDto();
        medDto2.setId(2L);
        medDto2.setName("Ibuprofen");
        medDto2.setSubstance("Ibuprofen");
        medDto2.setUnit("mg");
        medDto2.setManufacturer("Bayer");
    }

    @Test
    void create_ShouldReturnCreatedDto() throws Exception {
        MedicationDto in = new MedicationDto();
        in.setName("Paracetamol");
        in.setSubstance("Paracetamol");
        in.setUnit("mg");
        in.setManufacturer("GSK");

        when(medicationService.create(any(MedicationDto.class))).thenReturn(medDto1);

        mockMvc.perform(post("/api/medications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.manufacturer").value("GSK"));

        verify(medicationService, times(1)).create(any(MedicationDto.class));
    }

    @Test
    void getById_ShouldReturnDto() throws Exception {
        when(medicationService.findById(1L)).thenReturn(medDto1);

        mockMvc.perform(get("/api/medications/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.unit").value("mg"));

        verify(medicationService, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldReturnNotFound_WhenServiceThrows() throws Exception {
        when(medicationService.findById(99L))
                .thenThrow(new RuntimeException("Lek nie znaleziony: id=99"));

        mockMvc.perform(get("/api/medications/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Lek nie znaleziony: id=99")));

        verify(medicationService, times(1)).findById(99L);
    }

    @Test
    void update_ShouldReturnUpdatedDto() throws Exception {
        MedicationDto in = new MedicationDto();
        in.setName("Paracetamol Forte");
        in.setSubstance("Paracetamol");
        in.setUnit("mg");
        in.setManufacturer("GSK");

        MedicationDto updated = new MedicationDto();
        updated.setId(1L);
        updated.setName("Paracetamol Forte");
        updated.setSubstance("Paracetamol");
        updated.setUnit("mg");
        updated.setManufacturer("GSK");

        when(medicationService.update(eq(1L), any(MedicationDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/medications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Paracetamol Forte"));

        verify(medicationService, times(1)).update(eq(1L), any(MedicationDto.class));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(medicationService).delete(1L);

        mockMvc.perform(delete("/api/medications/1"))
                .andExpect(status().isNoContent());

        verify(medicationService, times(1)).delete(1L);
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        when(medicationService.findAll()).thenReturn(List.of(medDto1, medDto2));

        mockMvc.perform(get("/api/medications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name", is("Paracetamol")))
                .andExpect(jsonPath("$[1].name", is("Ibuprofen")));

        verify(medicationService, times(1)).findAll();
    }

    @Test
    void searchByName_ShouldReturnMatchingList() throws Exception {
        when(medicationService.searchByName("para")).thenReturn(List.of(medDto1));

        mockMvc.perform(get("/api/medications/search")
                        .param("name", "para")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].substance", is("Paracetamol")));

        verify(medicationService, times(1)).searchByName("para");
    }
}
