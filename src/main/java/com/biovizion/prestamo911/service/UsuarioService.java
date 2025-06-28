package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.entities.CreditoEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;

public interface UsuarioService {

    List<UsuarioEntity> findAll();
    UsuarioEntity save(UsuarioEntity usuario);

    Optional<UsuarioEntity> findById(Long id);
    Optional<UsuarioEntity> findByEmail(String email);
    public Optional<UsuarioEntity> get(Long id);
    public void update(UsuarioEntity usuario);
    public void delete(Long id);

    // metodo para validar código
    boolean existsByCodigo(String codigo);
}

