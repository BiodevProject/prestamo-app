package com.biovizion.prestamo911.service;


import com.biovizion.prestamo911.entities.AlertaEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface AlertaService {

    List<AlertaEntity> findAll();
    Optional<AlertaEntity> findById(Long id);
}