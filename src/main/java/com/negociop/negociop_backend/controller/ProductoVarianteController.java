package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import com.negociop.negociop_backend.service.MovimientoStockService;
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
    private final MovimientoStockService movimientoStockService;

    public ProductoVarianteController(
            ProductoVarianteRepository productoVarianteRepository,
            MovimientoStockService movimientoStockService
    ) {
        this.productoVarianteRepository = productoVarianteRepository;
        this.movimientoStockService = movimientoStockService;
    }

    // =========================================================
    // OBTENER TODAS
    // =========================================================

    @GetMapping
    public List<ProductoVariante> obtenerTodas() {
        return productoVarianteRepository.findAll();
    }

    // =========================================================
    // OBTENER POR ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Long id
    ) {

        var variante =
                productoVarianteRepository.findById(id);

        if (variante.isPresent()) {
            return ResponseEntity.ok(variante.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "error",
                                "Variante no encontrada",
                                "id",
                                id
                        )
                );
    }

    // =========================================================
    // CREAR
    // =========================================================

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody ProductoVariante variante
    ) {

        try {

            ProductoVariante nuevaVariante =
                    productoVarianteRepository.save(variante);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevaVariante);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "error",
                                    "Ya existe una variante para este producto, talle y color"
                            )
                    );
        }
    }

    // =========================================================
    // ACTUALIZAR VARIANTE COMPLETA
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoVariante variante
    ) {

        var resultado =
                productoVarianteRepository.findById(id);

        if (resultado.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "error",
                                    "Variante no encontrada",
                                    "id",
                                    id
                            )
                    );
        }

        ProductoVariante varianteExistente =
                resultado.get();

        varianteExistente.setProducto(
                variante.getProducto()
        );

        varianteExistente.setTalle(
                variante.getTalle()
        );

        varianteExistente.setColor(
                variante.getColor()
        );

        varianteExistente.setStock(
                variante.getStock()
        );

        try {

            ProductoVariante actualizada =
                    productoVarianteRepository.save(
                            varianteExistente
                    );

            return ResponseEntity.ok(actualizada);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "error",
                                    "Ya existe otra variante para este producto, talle y color"
                            )
                    );
        }
    }

    // =========================================================
    // ACTUALIZAR SOLO STOCK
    // =========================================================

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos
    ) {

        try {

            Object stockObject =
                    datos.get("stock");

            if (stockObject == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "El campo stock es obligatorio"
                                )
                        );
            }

            Integer nuevoStock;

            if (stockObject instanceof Number) {

                nuevoStock =
                        ((Number) stockObject).intValue();

            } else {

                nuevoStock =
                        Integer.valueOf(
                                stockObject.toString()
                        );
            }

            ProductoVariante actualizada =
                    movimientoStockService
                            .actualizarStockManual(
                                    id,
                                    nuevoStock
                            );

            return ResponseEntity.ok(actualizada);

        } catch (NumberFormatException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "El stock debe ser un número entero válido"
                            )
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id
    ) {

        if (!productoVarianteRepository.existsById(id)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "error",
                                    "Variante no encontrada",
                                    "id",
                                    id
                            )
                    );
        }

        productoVarianteRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // OBTENER POR PRODUCTO + TALLE + COLOR
    // =========================================================

    @GetMapping(
            "/producto/{productoId}/talle/{talleId}/color/{colorId}"
    )
    public ResponseEntity<?> obtenerPorProductoTalleColor(
            @PathVariable Long productoId,
            @PathVariable Long talleId,
            @PathVariable Long colorId
    ) {

        var resultado =
                productoVarianteRepository
                        .findByProductoIdAndTalleIdAndColorId(
                                productoId,
                                talleId,
                                colorId
                        );

        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "error",
                                "No existe una variante para ese producto, talle y color"
                        )
                );
    }

    // =========================================================
    // OBTENER VARIANTES DE UN PRODUCTO
    // =========================================================

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> obtenerPorProducto(
            @PathVariable Long productoId
    ) {

        List<ProductoVariante> variantes =
                productoVarianteRepository
                        .findByProductoId(productoId);

        return ResponseEntity.ok(variantes);
    }
}