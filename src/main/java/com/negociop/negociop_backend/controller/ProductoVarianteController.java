package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/variantes")
@CrossOrigin(origins = "*")
public class ProductoVarianteController {

    private final ProductoVarianteRepository productoVarianteRepository;

    public ProductoVarianteController(ProductoVarianteRepository productoVarianteRepository) {
        this.productoVarianteRepository = productoVarianteRepository;
    }

    // Obtener todas las variantes
    @GetMapping
    public List<ProductoVariante> obtenerTodas() {
        return productoVarianteRepository.findAll();
    }

    // Obtener una variante por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        return productoVarianteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body((ProductoVariante) Map.of(
                                        "error", "Variante no encontrada",
                                        "id", id
                                ))
                );
    }

    // Crear una variante
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoVariante variante) {

        try {

            ProductoVariante nuevaVariante =
                    productoVarianteRepository.save(variante);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevaVariante);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "Ya existe una variante para este producto, talle y color"
                    ));
        }
    }

    // Actualizar una variante
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoVariante variante
    ) {

        return productoVarianteRepository.findById(id)
                .map(varianteExistente -> {

                    varianteExistente.setProducto(variante.getProducto());
                    varianteExistente.setTalle(variante.getTalle());
                    varianteExistente.setColor(variante.getColor());
                    varianteExistente.setStock(variante.getStock());

                    try {

                        ProductoVariante actualizada =
                                productoVarianteRepository.save(varianteExistente);

                        return ResponseEntity.ok(actualizada);

                    } catch (DataIntegrityViolationException e) {

                        return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                        "error", "Ya existe otra variante para este producto, talle y color"
                                ));
                    }
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "error", "Variante no encontrada",
                                        "id", id
                                ))
                );
    }

    // Eliminar una variante
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        if (!productoVarianteRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Variante no encontrada",
                            "id", id
                    ));
        }

        productoVarianteRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{productoId}/talle/{talleId}/color/{colorId}")
    public ResponseEntity<?> obtenerPorProductoTalleColor(
            @PathVariable Long productoId,
            @PathVariable Long talleId,
            @PathVariable Long colorId
    ) {

        return productoVarianteRepository
                .findByProductoIdAndTalleIdAndColorId(
                        productoId,
                        talleId,
                        colorId
                )
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body((ProductoVariante) Map.of(
                                        "error", "No existe una variante para ese producto, talle y color"
                                ))
                );
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> obtenerPorProducto(
            @PathVariable Long productoId
    ) {
        List<ProductoVariante> variantes =
                productoVarianteRepository.findByProductoId(productoId);
        return ResponseEntity.ok(variantes);
    }
}