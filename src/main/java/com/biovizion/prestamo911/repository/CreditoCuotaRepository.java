package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE cc.credito.usuario.id = :usuarioId")
    List<CreditoCuotaEntity> findByUsuarioId(Long usuarioId);

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE LOWER(cc.estado) = 'enrevision'")
    List<CreditoCuotaEntity> findEnRevision();

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE LOWER(cc.estado) = 'avencer'")
    List<CreditoCuotaEntity> findAvencer();

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE LOWER(cc.estado) = 'vencido'")
    List<CreditoCuotaEntity> findVencidas();

    @Query("SELECT cc FROM CreditoCuotaEntity cc JOIN cc.credito c WHERE c.usuario.id = :usuarioId AND LOWER(cc.estado) = 'vencido'")
    List<CreditoCuotaEntity> findCuotasVencidasByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT cc FROM CreditoCuotaEntity cc WHERE cc.fechaVencimiento <= :currentDate AND LOWER(cc.estado) = 'pendiente'")
    List<CreditoCuotaEntity> findExpiredCuotas(@Param("currentDate") LocalDateTime currentDate);

    // Optimized query for large datasets - returns only IDs
    @Query("SELECT cc.id FROM CreditoCuotaEntity cc WHERE cc.fechaVencimiento <= :currentDate AND LOWER(cc.estado) = 'avencer'")
    List<Long> findExpiredCuotaIds(@Param("currentDate") LocalDateTime currentDate);

    // Find cuotas that are 1 week away from expiring (AVencer state)
    @Query("SELECT cc.id FROM CreditoCuotaEntity cc WHERE cc.fechaVencimiento <= :oneWeekFromNow AND cc.fechaVencimiento > :currentDate AND LOWER(cc.estado) = 'pendiente'")
    List<Long> findAboutToExpireCuotaIds(@Param("currentDate") LocalDateTime currentDate, @Param("oneWeekFromNow") LocalDateTime oneWeekFromNow);

    // Batch update for better performance
    @Query("UPDATE CreditoCuotaEntity cc SET cc.estado = 'Vencido' WHERE cc.id IN :ids")
    @Modifying
    @Transactional
    int updateCuotasToVencido(@Param("ids") List<Long> ids);

    // Batch update to AVencer state
    @Query("UPDATE CreditoCuotaEntity cc SET cc.estado = 'AVencer' WHERE cc.id IN :ids")
    @Modifying
    @Transactional
    int updateCuotasToAVencer(@Param("ids") List<Long> ids);
} 