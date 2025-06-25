package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;
import com.biovizion.prestamo911.repository.UsuarioSolicitudRepository;
import com.biovizion.prestamo911.service.UsuarioSolicitudService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioSolicitudImpl implements UsuarioSolicitudService {
    @Autowired
    private UsuarioSolicitudRepository usuarioSolicitudRepository;

    @Override
    public List<UsuarioSolicitudEntity> findAll() {
        return usuarioSolicitudRepository.findAll();
    }
    @Override
    public UsuarioSolicitudEntity save(UsuarioSolicitudEntity producto) {
        return usuarioSolicitudRepository.save(producto);
    }

    @Override
    public Optional<UsuarioSolicitudEntity> findById(Long id) {
        return usuarioSolicitudRepository.findById(id);
    }
    
    @Override
    public void update(UsuarioSolicitudEntity producto) {
        usuarioSolicitudRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        usuarioSolicitudRepository.deleteById(id);
    }
}