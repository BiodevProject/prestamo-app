package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;

public interface UsuarioSolicitudService {
    UsuarioSolicitudEntity save(UsuarioSolicitudEntity producto);
    List<UsuarioSolicitudEntity> findAll();
    Optional<UsuarioSolicitudEntity> findById(Long id);
    public void update(UsuarioSolicitudEntity producto);
    public void delete(Long id);
}
