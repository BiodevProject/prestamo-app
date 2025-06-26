package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biovizion.prestamo911.entities.TrabajadorEntity;

import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<TrabajadorEntity, Long> {

    Optional<TrabajadorEntity> findByEmail(String email);

} 