package com.biovizion.prestamo911.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.biovizion.prestamo911.DTO.UsuarioDTO;
import com.biovizion.prestamo911.entities.UsuarioEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByEmail(String email);


    @Query("""
       SELECT new com.biovizion.prestamo911.DTO.UsuarioDTO(
           u.idUsuario,
           u.nombre,
           u.edad,
           CASE 
               WHEN u.edad < 25 THEN "Joven"       
               WHEN u.edad < 50 THEN "Adulto"      
               ELSE "Anciano"                     
           END)
       FROM UsuarioEntity u
       """)
    List<UsuarioDTO> obtenerInfoUsuarios();

}
