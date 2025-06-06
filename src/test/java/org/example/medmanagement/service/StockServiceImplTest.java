package org.example.medmanagement.service;

import org.example.medmanagement.model.Medication;
import org.example.medmanagement.model.Stock;
import org.example.medmanagement.repository.MedicationRepository;
import org.example.medmanagement.repository.StockRepository;
import org.example.medmanagement.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Medication exampleMed;
    private Stock exampleStock;

    @BeforeEach
    void setUp() {
        // Przygotujmy „wzorcowy” lek
        exampleMed = new Medication();
        exampleMed.setId(2L);
        exampleMed.setName("TestMed");
        exampleMed.setSubstance("TestSub");
        exampleMed.setUnit("mg");
        exampleMed.setManufacturer("TestPharma");

        // I odpowiadający mu obiekt Stock
        exampleStock = new Stock();
        exampleStock.setId(5L);
        exampleStock.setMedication(exampleMed);
        exampleStock.setQuantity(100);
    }

    // 1) getByMedicationId(...) – wyjątki i ścieżka szczęśliwa

    @Test
    void getByMedicationId_ShouldThrow_WhenMedicationNotExist() {
        when(medicationRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> stockService.getByMedicationId(2L));
        assertTrue(ex.getMessage().contains("Lek nie istnieje: id=2"));
    }

    @Test
    void getByMedicationId_ShouldThrow_WhenStockNotFound() {
        // Lek istnieje
        when(medicationRepository.findById(2L)).thenReturn(Optional.of(exampleMed));
        // Ale stockRepository.findByMedication(...) → empty
        when(stockRepository.findByMedication(exampleMed)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> stockService.getByMedicationId(2L));
        assertTrue(ex.getMessage().contains("Stan magazynowy nie znaleziony dla leku: id=2"));
    }

    @Test
    void getByMedicationId_ShouldReturnStock_WhenExists() {
        when(medicationRepository.findById(2L)).thenReturn(Optional.of(exampleMed));
        when(stockRepository.findByMedication(exampleMed)).thenReturn(Optional.of(exampleStock));

        Stock result = stockService.getByMedicationId(2L);
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(100, result.getQuantity());
    }

    // 2) updateQuantity(...) – happy path

    @Test
    void updateQuantity_ShouldModifyQuantityAndSave() {
        // Przygotujmy stub getByMedicationId(2L)
        when(medicationRepository.findById(2L)).thenReturn(Optional.of(exampleMed));
        when(stockRepository.findByMedication(exampleMed)).thenReturn(Optional.of(exampleStock));
        // Kiedy save(...) → zwracamy obiekt z nową ilością
        Stock updated = new Stock();
        updated.setId(5L);
        updated.setMedication(exampleMed);
        updated.setQuantity(42);
        when(stockRepository.save(any(Stock.class))).thenReturn(updated);

        // Act
        Stock result = stockService.updateQuantity(2L, 42);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(42, result.getQuantity());

        ArgumentCaptor<Stock> capt = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepository, times(1)).save(capt.capture());
        Stock passed = capt.getValue();
        assertEquals(42, passed.getQuantity());
        assertEquals(2L, passed.getMedication().getId());
    }

    // 3) findAll(...) – proste przekierowanie

    @Test
    void findAll_ShouldReturnListOfStocks() {
        Stock s1 = new Stock();
        s1.setId(7L);
        s1.setMedication(exampleMed);
        s1.setQuantity(50);

        when(stockRepository.findAll()).thenReturn(Arrays.asList(exampleStock, s1));
        List<Stock> list = stockService.findAll();

        assertEquals(2, list.size());
        Set<Long> ids = new HashSet<>();
        list.forEach(st -> ids.add(st.getId()));
        assertTrue(ids.contains(5L));
        assertTrue(ids.contains(7L));
    }
}
