package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biovizion.prestamo911.entities.CreditoEntity;

public interface CreditoRepository extends JpaRepository<CreditoEntity, Long> {
}
