package com.biovizion.prestamo911.service.impl;

import com.biovizion.prestamo911.entities.AlertaEntity;
import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.repository.AlertaRepository;
import com.biovizion.prestamo911.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertaServiceImpl implements AlertaService {

    @Autowired
    private  AlertaRepository alertaRepository;


    @Override
    public List<AlertaEntity> findAll() {
        return alertaRepository.findAll();
    }

    @Override
    public Optional<AlertaEntity> findById(Long id) {
        return alertaRepository.findById(id);
    }
}