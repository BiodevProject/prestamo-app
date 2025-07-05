package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.repository.CreditoCuotaRepository;
import com.biovizion.prestamo911.service.CreditoCuotaService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CreditoCuotaImpl implements CreditoCuotaService {
    
    @Autowired
    private CreditoCuotaRepository creditoCuotaRepository;

    @Override
    public List<CreditoCuotaEntity> findAll() {
        return creditoCuotaRepository.findAll();
    }

    @Override
    public CreditoCuotaEntity save(CreditoCuotaEntity creditoCuota) {
        return creditoCuotaRepository.save(creditoCuota);
    }

    @Override
    public Optional<CreditoCuotaEntity> findById(Long id) {
        return creditoCuotaRepository.findById(id);
    }
    
    @Override
    public void update(CreditoCuotaEntity creditoCuota) {
        creditoCuotaRepository.save(creditoCuota);
    }

    @Override
    public void delete(Long id) {
        creditoCuotaRepository.deleteById(id);
    }

    @Override
    public List<CreditoCuotaEntity> findByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findPendientes() {
        return creditoCuotaRepository.findPendientes();
    }

    @Override
    public List<CreditoCuotaEntity> findPagadas() {
        return creditoCuotaRepository.findPagadas();
    }

    @Override
    public List<CreditoCuotaEntity> findVencidas() {
        return creditoCuotaRepository.findVencidas();
    }

    @Override
    public List<CreditoCuotaEntity> findAVencer() {
        return creditoCuotaRepository.findAVencer();
    }

    @Override
    public List<CreditoCuotaEntity> findByUsuarioId(Long usuarioId) {
        return creditoCuotaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<CreditoCuotaEntity> findEnRevision() {
        return creditoCuotaRepository.findEnRevision();
    }

    @Override
    public List<CreditoCuotaEntity> findPendientesByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findPendientesByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findEnRevisionByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findEnRevisionByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findPagadasByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findPagadasByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findVencidasByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findVencidasByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findAVencerByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findAVencerByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findByUsuarioIdAndEstado(Long usuarioId, String estado) {
        return creditoCuotaRepository.findCuotasByUsuarioIdAndEstado(usuarioId, estado);
    }

    public void updateExpiredCuotas() {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Long> expiredCuotaIds = creditoCuotaRepository.findExpiredCuotaIds(currentDate);
        
        if (!expiredCuotaIds.isEmpty()) {
            processBatch(expiredCuotaIds, "expired", "Vencido");
        } else {
            System.out.println("No expired cuotas found");
        }
    }

    @Override
    public void updateAboutToExpireCuotas() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime oneWeekFromNow = currentDate.plus(7, ChronoUnit.DAYS);
        List<Long> aboutToExpireCuotaIds = creditoCuotaRepository.findAboutToExpireCuotaIds(currentDate, oneWeekFromNow);
        
        if (!aboutToExpireCuotaIds.isEmpty()) {
            processBatch(aboutToExpireCuotaIds, "about to expire", "AVencer");
        } else {
            System.out.println("No cuotas about to expire found");
        }
    }

    // Helper method to process batches (KISS - DRY principle)
    private void processBatch(List<Long> cuotaIds, String description, String newState) {
        int batchSize = 1000;
        int totalUpdated = 0;
        
        for (int i = 0; i < cuotaIds.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, cuotaIds.size());
            List<Long> batch = cuotaIds.subList(i, endIndex);
            
            int updatedCount = newState.equals("Vencido") 
                ? creditoCuotaRepository.updateCuotasToVencido(batch)
                : creditoCuotaRepository.updateCuotasToAVencer(batch);
            
            totalUpdated += updatedCount;
        }
        
        System.out.println("Updated " + totalUpdated + " " + description + " cuotas to " + newState);
    }
} 