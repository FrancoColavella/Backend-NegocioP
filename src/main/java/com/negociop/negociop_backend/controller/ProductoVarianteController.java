package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variantes")
@CrossOrigin(origins = "*")
public class ProductoVarianteController {

    private final ProductoVarianteRepository productoVarianteRepository;

    public ProductoVarianteController(ProductoVarianteRepository productoVarianteRepository) {
        this.productoVarianteRepository = productoVarianteRepository;
    }

    @GetMapping
    public List<ProductoVariante> obtenerTodos() {
        return productoVarianteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ProductoVariante obtenerPorId(@PathVariable Long id) {

        return productoVarianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));
    }

    @PostMapping
    public ProductoVariante crear(@RequestBody ProductoVariante variante) {
        return productoVarianteRepository.save(variante);
    }

    @PutMapping("/{id}")
    public ProductoVariante actualizar(
            @PathVariable Long id,
            @RequestBody ProductoVariante varianteActualizada) {

        ProductoVariante variante = productoVarianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));

        variante.setProducto(varianteActualizada.getProducto());
        variante.setTalle(varianteActualizada.getTalle());
        variante.setColor(varianteActualizada.getColor());
        variante.setStock(varianteActualizada.getStock());

        return productoVarianteRepository.save(variante);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {

        ProductoVariante variante = productoVarianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));

        productoVarianteRepository.delete(variante);
    }
}