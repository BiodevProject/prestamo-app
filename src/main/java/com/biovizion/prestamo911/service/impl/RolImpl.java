package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.RolEntity;
import com.biovizion.prestamo911.repository.RolRepository;
import com.biovizion.prestamo911.service.RolService;

import java.util.List;

@Service
public class RolImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<RolEntity> findAll() {
        return rolRepository.findAll();
    }
    @Override
    public RolEntity save(RolEntity rol) {
        return rolRepository.save(rol);
    }
}
