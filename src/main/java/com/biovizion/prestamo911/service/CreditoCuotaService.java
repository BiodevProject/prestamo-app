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
    
    List<CreditoCuotaEntity> findByCreditoId(Long creditoId);
    List<CreditoCuotaEntity> findPendientes(Long creditoId);
    List<CreditoCuotaEntity> findPagadas(Long creditoId);
    List<CreditoCuotaEntity> findVencidas(Long creditoId);
    List<CreditoCuotaEntity> findByUsuarioId(Long usuarioId);
    List<CreditoCuotaEntity> findEnRevision();
    List<CreditoCuotaEntity> findAvencer();
    List<CreditoCuotaEntity> findVencidas();

    List<CreditoCuotaEntity> getCuotasByUsuarioAndEstado(Long usuarioId, String estado);
} 