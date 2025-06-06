package org.example.medmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedManagementApplication.class, args);
    }
}
