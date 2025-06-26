package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.entities.TrabajadorEntity;

public interface TrabajadorService {

    List<TrabajadorEntity> findAll();
    TrabajadorEntity save(TrabajadorEntity trabajador);

    Optional<TrabajadorEntity> findById(Long id);
    Optional<TrabajadorEntity> findByEmail(String email);
    public Optional<TrabajadorEntity> get(Long id);
    public void update(TrabajadorEntity trabajador);
    public void delete(Long id);



} 