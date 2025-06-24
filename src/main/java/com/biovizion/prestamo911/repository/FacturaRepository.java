package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biovizion.prestamo911.entities.FacturaEntity;

@Repository
public interface FacturaRepository extends JpaRepository<FacturaEntity, Long> {
}
