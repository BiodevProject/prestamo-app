package com.biovizion.prestamo911.service;

import java.util.List;

import com.biovizion.prestamo911.entities.RolEntity;

public interface RolService {

    List<RolEntity> findAll();
    RolEntity save(RolEntity rol);
}

