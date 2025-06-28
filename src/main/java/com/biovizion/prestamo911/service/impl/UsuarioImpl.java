package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.biovizion.prestamo911.entities.UsuarioEntity;
import com.biovizion.prestamo911.repository.UsuarioRepository;
import com.biovizion.prestamo911.service.UsuarioService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }
    @Override
    public UsuarioEntity save(UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<UsuarioEntity> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<UsuarioEntity> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Optional<UsuarioEntity> get(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public void update(UsuarioEntity usuario) {
        usuarioRepository.save(usuario);

    }

    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);

    }


    @Override
    public boolean existsByCodigo(String codigo) {
        return usuarioRepository.existsByCodigo(codigo);
    }

}
