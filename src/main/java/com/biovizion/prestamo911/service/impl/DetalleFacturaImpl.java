package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.DetalleFacturaEntity;
import com.biovizion.prestamo911.repository.DetalleFacturaRepository;
import com.biovizion.prestamo911.service.DetalleFacturaService;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleFacturaImpl implements DetalleFacturaService {
    @Autowired
    private DetalleFacturaRepository facturaRepository;

    @Override
    public List<DetalleFacturaEntity> findAll() {
        return facturaRepository.findAll();
    }
    @Override
    public DetalleFacturaEntity save(DetalleFacturaEntity factura) {
        return facturaRepository.save(factura);
    }

    @Override
    public Optional<DetalleFacturaEntity> findById(Long id) {
        return facturaRepository.findById(id);
    }
    
    @Override
    public void update(DetalleFacturaEntity factura) {
        facturaRepository.save(factura);
    }

    @Override
    public void delete(Long id) {
        facturaRepository.deleteById(id);
    }
}