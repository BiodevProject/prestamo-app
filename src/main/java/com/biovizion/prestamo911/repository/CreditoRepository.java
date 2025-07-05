package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biovizion.prestamo911.entities.CreditoEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditoRepository extends JpaRepository<CreditoEntity, Long> {

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Pendiente'")
    List<CreditoEntity> findPendientes();

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Aceptado'")
    List<CreditoEntity> findAceptados();

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Rechazado'")
    List<CreditoEntity> findRechazados();

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Finalizado'")
    List<CreditoEntity> findFinalizados();

    @Query("SELECT c FROM CreditoEntity c WHERE c.usuario.id = :id")
    List<CreditoEntity> findByUsuarioId(Long id);

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Pendiente' AND c.usuario.id = :id")
    List<CreditoEntity> findPendientesByUsuarioId(Long id);

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Aceptado' AND c.usuario.id = :id")
    List<CreditoEntity> findAceptadosByUsuarioId(Long id);

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Rechazado' AND c.usuario.id = :id")
    List<CreditoEntity> findRechazadosByUsuarioId(Long id);

    @Query("SELECT c FROM CreditoEntity c WHERE LOWER(c.estado) = 'Finalizado' AND c.usuario.id = :id")
    List<CreditoEntity> findFinalizadosByUsuarioId(Long id);
}
