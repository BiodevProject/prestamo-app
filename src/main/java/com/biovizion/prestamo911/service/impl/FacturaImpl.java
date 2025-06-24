package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.FacturaEntity;
import com.biovizion.prestamo911.repository.FacturaRepository;
import com.biovizion.prestamo911.service.FacturaService;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaImpl implements FacturaService {
    @Autowired
    private FacturaRepository facturaRepository;

    @Override
    public List<FacturaEntity> findAll() {
        return facturaRepository.findAll();
    }
    @Override
    public FacturaEntity save(FacturaEntity factura) {
        return facturaRepository.save(factura);
    }

    @Override
    public Optional<FacturaEntity> findById(Long id) {
        return facturaRepository.findById(id);
    }
    
    @Override
    public void update(FacturaEntity factura) {
        facturaRepository.save(factura);
    }

    @Override
    public void delete(Long id) {
        facturaRepository.deleteById(id);
    }
}