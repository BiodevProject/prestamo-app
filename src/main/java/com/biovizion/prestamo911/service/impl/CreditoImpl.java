package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.repository.CreditoRepository;
import com.biovizion.prestamo911.service.CreditoService;

import java.util.List;
import java.util.Optional;

@Service
public class CreditoImpl implements CreditoService {

    @Autowired
    private CreditoRepository creditoRepository;

    @Override
    public List<CreditoEntity> findAll() {
        return creditoRepository.findAll();
    }
    @Override
    public CreditoEntity save(CreditoEntity producto) {
        return creditoRepository.save(producto);
    }

    @Override
    public Optional<CreditoEntity> findById(Long id) {
        return creditoRepository.findById(id);
    }
    
    @Override
    public void update(CreditoEntity producto) {
        creditoRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        creditoRepository.deleteById(id);
    }

    @Override
    public List<CreditoEntity> findPendientes() {
        return creditoRepository.findPendientes();
    }

    @Override
    public List<CreditoEntity> findAceptados() {
        return creditoRepository.findAceptados();
    }

    @Override
    public List<CreditoEntity> findRechazados() {
        return creditoRepository.findRechazados();
    }

    @Override
    public List<CreditoEntity> findFinalizados() {
        return creditoRepository.findFinalizados();
    }

    @Override
    public List<CreditoEntity> findByUsuarioId(Long id) {
        return creditoRepository.findByUsuarioId(id);
    }

    @Override
    public List<CreditoEntity> findPendientesByUsuarioId(Long id) {
        return creditoRepository.findPendientesByUsuarioId(id);
    }

    @Override
    public List<CreditoEntity> findAceptadosByUsuarioId(Long id) {
        return creditoRepository.findAceptadosByUsuarioId(id);
    }

    @Override
    public List<CreditoEntity> findRechazadosByUsuarioId(Long id) {
        return creditoRepository.findRechazadosByUsuarioId(id);
    }

    @Override
    public List<CreditoEntity> findFinalizadosByUsuarioId(Long id) {
        return creditoRepository.findFinalizadosByUsuarioId(id);
    }
}