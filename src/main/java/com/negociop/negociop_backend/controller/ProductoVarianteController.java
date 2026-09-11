package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Color;
import com.negociop.negociop_backend.entity.Producto;
import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.entity.Talle;
import com.negociop.negociop_backend.repository.ColorRepository;
import com.negociop.negociop_backend.repository.ProductoRepository;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import com.negociop.negociop_backend.repository.TalleRepository;
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
    private final ProductoRepository productoRepository;
    private final TalleRepository talleRepository;
    private final ColorRepository colorRepository;

    public ProductoVarianteController(
            ProductoVarianteRepository productoVarianteRepository,
            ProductoRepository productoRepository,
            TalleRepository talleRepository,
            ColorRepository colorRepository
    ) {
        this.productoVarianteRepository = productoVarianteRepository;
        this.productoRepository = productoRepository;
        this.talleRepository = talleRepository;
        this.colorRepository = colorRepository;
    }

    @GetMapping
    public List<ProductoVariante> obtenerTodas() {
        return productoVarianteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        ProductoVariante variante =
                productoVarianteRepository.findById(id).orElse(null);

        if (variante == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Variante no encontrada",
                            "id", id
                    ));
        }

        return ResponseEntity.ok(variante);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoVariante variante) {

        try {

            if (variante.getProducto() == null ||
                    variante.getProducto().getId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe indicar el producto"));
            }

            if (variante.getTalle() == null ||
                    variante.getTalle().getId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe indicar el talle"));
            }

            if (variante.getColor() == null ||
                    variante.getColor().getId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe indicar el color"));
            }

            if (variante.getStock() == null ||
                    variante.getStock() < 0) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El stock no puede ser negativo"));
            }

            Producto producto = productoRepository
                    .findById(variante.getProducto().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado"));

            Talle talle = talleRepository
                    .findById(variante.getTalle().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Talle no encontrado"));

            Color color = colorRepository
                    .findById(variante.getColor().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Color no encontrado"));

            variante.setProducto(producto);
            variante.setTalle(talle);
            variante.setColor(color);

            ProductoVariante nuevaVariante =
                    productoVarianteRepository.save(variante);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevaVariante);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "Ya existe una variante para este producto, talle y color"
                    ));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoVariante variante
    ) {

        ProductoVariante varianteExistente =
                productoVarianteRepository.findById(id).orElse(null);

        if (varianteExistente == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Variante no encontrada",
                            "id", id
                    ));
        }

        try {

            if (variante.getProducto() == null ||
                    variante.getProducto().getId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe indicar el producto"));
            }

            if (variante.getTalle() == null ||
                    variante.getTalle().getId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe indicar el talle"));
            }

            if (variante.getColor() == null ||
                    variante.getColor().getId() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe indicar el color"));
            }

            if (variante.getStock() == null ||
                    variante.getStock() < 0) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El stock no puede ser negativo"));
            }

            Producto producto = productoRepository
                    .findById(variante.getProducto().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado"));

            Talle talle = talleRepository
                    .findById(variante.getTalle().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Talle no encontrado"));

            Color color = colorRepository
                    .findById(variante.getColor().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Color no encontrado"));

            varianteExistente.setProducto(producto);
            varianteExistente.setTalle(talle);
            varianteExistente.setColor(color);
            varianteExistente.setStock(variante.getStock());

            ProductoVariante actualizada =
                    productoVarianteRepository.save(varianteExistente);

            return ResponseEntity.ok(actualizada);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "Ya existe otra variante para este producto, talle y color"
                    ));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

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

        ProductoVariante variante =
                productoVarianteRepository
                        .findByProductoIdAndTalleIdAndColorId(
                                productoId,
                                talleId,
                                colorId
                        )
                        .orElse(null);

        if (variante == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Variante no encontrada",
                            "productoId", productoId,
                            "talleId", talleId,
                            "colorId", colorId
                    ));
        }

        return ResponseEntity.ok(variante);
    }
}