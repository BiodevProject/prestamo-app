package com.biovizion.prestamo911.repository;

import com.biovizion.prestamo911.entities.DetalleFacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleFacturaRepository  extends JpaRepository<DetalleFacturaEntity, Long> {
}
