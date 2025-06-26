package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.biovizion.prestamo911.entities.TrabajadorEntity;
import com.biovizion.prestamo911.repository.TrabajadorRepository;
import com.biovizion.prestamo911.service.TrabajadorService;

import java.util.List;
import java.util.Optional;

@Service
public class TrabajadorImpl implements TrabajadorService {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Override
    public List<TrabajadorEntity> findAll() {
        return trabajadorRepository.findAll();
    }
    @Override
    public TrabajadorEntity save(TrabajadorEntity trabajador) {
        return trabajadorRepository.save(trabajador);
    }

    @Override
    public Optional<TrabajadorEntity> findById(Long id) {
        return trabajadorRepository.findById(id);
    }

    @Override
    public Optional<TrabajadorEntity> findByEmail(String email) {
        return trabajadorRepository.findByEmail(email);
    }

    @Override
    public Optional<TrabajadorEntity> get(Long id) {
        return trabajadorRepository.findById(id);
    }

    @Override
    public void update(TrabajadorEntity trabajador) {
        trabajadorRepository.save(trabajador);

    }

    @Override
    public void delete(Long id) {
        trabajadorRepository.deleteById(id);

    }

} 