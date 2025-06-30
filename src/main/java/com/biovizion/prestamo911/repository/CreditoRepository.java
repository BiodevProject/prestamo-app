package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biovizion.prestamo911.entities.CreditoEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditoRepository extends JpaRepository<CreditoEntity, Long> {

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Pendiente'")
    List<CreditoEntity> findPendientes();

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Aceptado'")
    List<CreditoEntity> findAceptadas();

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Rechazado'")
    List<CreditoEntity> findRechazados();

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Finalizado'")
    List<CreditoEntity> findFinalizados();


    List<CreditoEntity> findByUsuarioId(Long id);
}
