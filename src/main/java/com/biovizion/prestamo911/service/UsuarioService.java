package com.biovizion.prestamo911.service;

import java.util.List;
import java.util.Optional;

import com.biovizion.prestamo911.DTO.UsuarioDTO;
import com.biovizion.prestamo911.entities.UsuarioEntity;

public interface UsuarioService {

    List<UsuarioEntity> findAll();
    UsuarioEntity save(UsuarioEntity usuario);

    Optional<UsuarioEntity> findById(Integer id);
    Optional<UsuarioEntity> findByEmail(String email);
    public Optional<UsuarioEntity> get(Integer id);
    public void update(UsuarioEntity usuario);
    public void delete(Integer id);

    List<UsuarioDTO> obtenerInfoUsuarios();

}

