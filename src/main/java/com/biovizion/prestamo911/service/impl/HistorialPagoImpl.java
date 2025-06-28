package com.biovizion.prestamo911.service.impl;

import com.biovizion.prestamo911.entities.HistorialPagoEntity;
import com.biovizion.prestamo911.repository.HistorialPagoRepository;
import com.biovizion.prestamo911.service.HistorialPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialPagoImpl implements HistorialPagoService {

    @Autowired
    HistorialPagoRepository historialPagoRepository;

    @Override
    public HistorialPagoEntity save(HistorialPagoEntity historialpago) {
        return null;
    }

    @Override
    public List<HistorialPagoEntity> findAll() {
        return historialPagoRepository.findAll();
    }

    @Override
    public Optional<HistorialPagoEntity> findById(Long id) {
        return historialPagoRepository.findById(id);
    }

    @Override
    public void update(HistorialPagoEntity historialpago) {
        historialPagoRepository.save(historialpago);
    }

    @Override
    public void delete(Long id) {
        historialPagoRepository.deleteById(id);
    }
}
