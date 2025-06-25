package com.biovizion.prestamo911.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biovizion.prestamo911.entities.ProductoEntity;
import com.biovizion.prestamo911.repository.ProductoRepository;
import com.biovizion.prestamo911.service.ProductoService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoImpl implements ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<ProductoEntity> findAll() {
        return productoRepository.findAll();
    }
    @Override
    public ProductoEntity save(ProductoEntity producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Optional<ProductoEntity> findById(Long id) {
        return productoRepository.findById(id);
    }
    @Override
    public Optional<ProductoEntity> findByNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }
    
    @Override
    public void update(ProductoEntity producto) {
        productoRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        productoRepository.deleteById(id);
    }
}