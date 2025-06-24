package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.entities.FacturaEntity;

public interface FacturaService {
    FacturaEntity save(FacturaEntity usuario);
    List<FacturaEntity> findAll();
    Optional<FacturaEntity> findById(Long id);
    public void update(FacturaEntity usuario);
    public void delete(Long id);
}

