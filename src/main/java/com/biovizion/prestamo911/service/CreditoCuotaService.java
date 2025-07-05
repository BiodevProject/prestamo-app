package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;

public interface CreditoCuotaService {
    CreditoCuotaEntity save(CreditoCuotaEntity creditoCuota);
    List<CreditoCuotaEntity> findAll();
    Optional<CreditoCuotaEntity> findById(Long id);
    void update(CreditoCuotaEntity creditoCuota);
    void delete(Long id);
    void updateExpiredCuotas();
    void updateAboutToExpireCuotas();
    
    List<CreditoCuotaEntity> findPendientes();
    List<CreditoCuotaEntity> findEnRevision();
    List<CreditoCuotaEntity> findAVencer();
    List<CreditoCuotaEntity> findVencidas();
    List<CreditoCuotaEntity> findPagadas();

    List<CreditoCuotaEntity> findByUsuarioId(Long usuarioId);
    List<CreditoCuotaEntity> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    
    List<CreditoCuotaEntity> findByCreditoId(Long creditoId);
    List<CreditoCuotaEntity> findPendientesByCreditoId(Long creditoId);
    List<CreditoCuotaEntity> findEnRevisionByCreditoId(Long creditoId);
    List<CreditoCuotaEntity> findPagadasByCreditoId(Long creditoId);
    List<CreditoCuotaEntity> findVencidasByCreditoId(Long creditoId);
    List<CreditoCuotaEntity> findAVencerByCreditoId(Long creditoId);
} 