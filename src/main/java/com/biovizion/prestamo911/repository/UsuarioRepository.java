package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biovizion.prestamo911.entities.UsuarioEntity;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByCodigo(String codigo);

}
