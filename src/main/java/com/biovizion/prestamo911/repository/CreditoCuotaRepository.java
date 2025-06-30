package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;

import java.util.List;

public interface CreditoCuotaRepository extends JpaRepository<CreditoCuotaEntity, Long> {

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE cc.credito.id = :creditoId")
    List<CreditoCuotaEntity> findByCreditoId(Long creditoId);

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE cc.credito.id = :creditoId AND LOWER(cc.estado) = 'pendiente'")
    List<CreditoCuotaEntity> findPendientes(Long creditoId);

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE cc.credito.id = :creditoId AND LOWER(cc.estado) = 'pagado'")
    List<CreditoCuotaEntity> findPagadas(Long creditoId);

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE cc.credito.id = :creditoId AND LOWER(cc.estado) = 'vencido'")
    List<CreditoCuotaEntity> findVencidas(Long creditoId);
} 