package org.example.medmanagement.repository;

import org.example.medmanagement.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repozytorium JPA dla encji Reminder.
 */
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    /**
     * Pobierz wszystkie przypomnienia, których czas remindTime jest przed beforeTime i sent=false.
     */
    List<Reminder> findByRemindTimeBeforeAndSentFalse(LocalDateTime beforeTime);

    /**
     * Pobierz wszystkie przypomnienia (encje) należące do recepty o danym ID.
     */
    List<Reminder> findByPrescriptionId(Long prescriptionId);
}
