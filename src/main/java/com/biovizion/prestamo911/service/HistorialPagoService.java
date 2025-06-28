package com.biovizion.prestamo911.service;


import com.biovizion.prestamo911.entities.HistorialPagoEntity;

import java.util.List;
import java.util.Optional;


public interface HistorialPagoService {

    HistorialPagoEntity save(HistorialPagoEntity historialpago);
    List<HistorialPagoEntity> findAll();
    Optional<HistorialPagoEntity> findById(Long id);
    public void update(HistorialPagoEntity historialpago);
    public void delete(Long id);

}
