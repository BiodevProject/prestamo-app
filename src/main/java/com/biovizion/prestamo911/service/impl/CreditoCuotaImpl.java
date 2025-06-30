package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.biovizion.prestamo911.repository.CreditoCuotaRepository;
import com.biovizion.prestamo911.service.CreditoCuotaService;

import java.util.List;
import java.util.Optional;

@Service
public class CreditoCuotaImpl implements CreditoCuotaService {
    
    @Autowired
    private CreditoCuotaRepository creditoCuotaRepository;

    @Override
    public List<CreditoCuotaEntity> findAll() {
        return creditoCuotaRepository.findAll();
    }

    @Override
    public CreditoCuotaEntity save(CreditoCuotaEntity creditoCuota) {
        return creditoCuotaRepository.save(creditoCuota);
    }

    @Override
    public Optional<CreditoCuotaEntity> findById(Long id) {
        return creditoCuotaRepository.findById(id);
    }
    
    @Override
    public void update(CreditoCuotaEntity creditoCuota) {
        creditoCuotaRepository.save(creditoCuota);
    }

    @Override
    public void delete(Long id) {
        creditoCuotaRepository.deleteById(id);
    }

    @Override
    public List<CreditoCuotaEntity> findByCreditoId(Long creditoId) {
        return creditoCuotaRepository.findByCreditoId(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findPendientes(Long creditoId) {
        return creditoCuotaRepository.findPendientes(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findPagadas(Long creditoId) {
        return creditoCuotaRepository.findPagadas(creditoId);
    }

    @Override
    public List<CreditoCuotaEntity> findVencidas(Long creditoId) {
        return creditoCuotaRepository.findVencidas(creditoId);
    }
} 