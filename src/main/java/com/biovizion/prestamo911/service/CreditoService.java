package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.entities.CreditoEntity;

public interface CreditoService {
    CreditoEntity save(CreditoEntity producto);
    List<CreditoEntity> findAll();
    Optional<CreditoEntity> findById(Long id);
    public void update(CreditoEntity producto);
    public void delete(Long id);
    List<CreditoEntity> findPendientes();
    List<CreditoEntity> findAceptadas();
    List<CreditoEntity> findByUsuarioId(Long id);
}
