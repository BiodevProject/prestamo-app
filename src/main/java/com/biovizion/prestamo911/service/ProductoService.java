package com.biovizion.prestamo911.service;

import com.biovizion.prestamo911.entities.ProductoEntity;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    ProductoEntity save(ProductoEntity producto);
    List<ProductoEntity> findAll();
    Optional<ProductoEntity> findById(Long id);
    Optional<ProductoEntity> findByNombre(String nombre);
    public void update(ProductoEntity producto);
    public void delete(Long id);
}
