package com.biovizion.prestamo911.repository;



import com.biovizion.prestamo911.entities.AlertaEntity;
import com.biovizion.prestamo911.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertaRepository extends JpaRepository<AlertaEntity, Long> {

    Optional<AlertaEntity> findById(Long id);

}
