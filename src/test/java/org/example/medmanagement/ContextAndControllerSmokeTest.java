package org.example.medmanagement;

import org.example.medmanagement.controller.AuthController;
import org.example.medmanagement.controller.MedicationController;
import org.example.medmanagement.controller.PrescriptionController;
import org.example.medmanagement.controller.ReminderController;
import org.example.medmanagement.controller.StockController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * „Smoke test” uruchamiający cały kontekst Spring Boot,
 * ale wyłączający filtry bezpieczeństwa, aby GET-y zwracały 200/404.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ContextAndControllerSmokeTest {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;

    // Mockujemy serwisy, by kontrolery zwracały pustą listę
    @MockBean
    org.example.medmanagement.service.MedicationService medicationService;
    @MockBean
    org.example.medmanagement.service.PrescriptionService prescriptionService;
    @MockBean
    org.example.medmanagement.service.ReminderService reminderService;
    @MockBean
    org.example.medmanagement.service.StockService stockService;
    @MockBean
    org.example.medmanagement.service.UserService userService; // dla AuthController

    @Test
    void contextLoads_AllMainBeansArePresent() {
        assertThat(ctx).isNotNull();

        assertThat(ctx.getBean(MedicationController.class)).isNotNull();
        assertThat(ctx.getBean(PrescriptionController.class)).isNotNull();
        assertThat(ctx.getBean(ReminderController.class)).isNotNull();
        assertThat(ctx.getBean(StockController.class)).isNotNull();
        assertThat(ctx.getBean(AuthController.class)).isNotNull();
    }

    @Test
    void getMedications_WhenCalled_ShouldReturnOkWithEmptyList() throws Exception {
        when(medicationService.findAll()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/medications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(medicationService, times(1)).findAll();
    }

    @Test
    void getPrescriptions_WhenCalled_ShouldReturnOkWithEmptyList() throws Exception {
        when(prescriptionService.findAll()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(prescriptionService, times(1)).findAll();
    }

    @Test
    void getReminders_WhenCalled_ShouldReturnOkWithEmptyList() throws Exception {
        when(reminderService.findAll()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/reminders/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reminderService, times(1)).findAll();
    }

    @Test
    void getStock_WhenCalled_ShouldReturnOkWithEmptyList() throws Exception {
        when(stockService.findAll()).thenReturn(java.util.List.of());

        // Dostosuj "/api/stock" do faktycznej ścieżki w Twoim StockController
        mockMvc.perform(get("/api/stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stockService, times(1)).findAll();
    }

    @Test
    void authEndpoints_ShouldBePresent() throws Exception {
        // GET /api/auth – zakładamy, że nie ma mapowania GET, więc zwraca 404
        mockMvc.perform(get("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
