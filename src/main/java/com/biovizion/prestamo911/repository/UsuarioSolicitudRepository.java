package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biovizion.prestamo911.entities.UsuarioSolicitudEntity;

public interface UsuarioSolicitudRepository extends JpaRepository<UsuarioSolicitudEntity, Long> {
}
