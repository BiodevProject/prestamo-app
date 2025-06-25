package com.biovizion.prestamo911.service;

import com.biovizion.prestamo911.entities.DetalleFacturaEntity;

import java.util.List;
import java.util.Optional;
public interface DetalleFacturaService {
    DetalleFacturaEntity save(DetalleFacturaEntity detallefactura);
    List<DetalleFacturaEntity> findAll();
    Optional<DetalleFacturaEntity> findById(Long id);
    public void update(DetalleFacturaEntity detallefactura);
    public void delete(Long id);
}
