package com.biovizion.prestamo911;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.biovizion.prestamo911.service.CreditoCuotaService;

@Component
public class ScheduledTasks {
    
    @Autowired
    private CreditoCuotaService creditoCuotaService;
    
    // Run daily at midnight (00:00) El Salvador time
    @Scheduled(cron = "0 51 16 * * ?", zone = "America/El_Salvador")
    public void checkExpiredCuotas() {
        try {
            System.out.println("Starting scheduled task at " + java.time.LocalDateTime.now());
            
            creditoCuotaService.updateAboutToExpireCuotas();
            creditoCuotaService.updateExpiredCuotas();
            
            System.out.println("Scheduled task completed successfully");
        } catch (Exception e) {
            System.err.println("Error in scheduled task: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 