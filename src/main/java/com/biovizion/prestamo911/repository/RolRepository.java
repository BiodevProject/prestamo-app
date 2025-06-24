package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biovizion.prestamo911.entities.RolEntity;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Integer> {
}
