package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Color;
import com.negociop.negociop_backend.repository.ColorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/colores")
@CrossOrigin(origins = "*")
public class ColorController {

    private final ColorRepository colorRepository;

    private static final Pattern HEX_PATTERN =
            Pattern.compile("^#[0-9A-Fa-f]{6}$");

    public ColorController(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    // ==========================================
    // OBTENER TODOS
    // ==========================================

    @GetMapping
    public List<Color> obtenerTodos() {
        return colorRepository.findAll();
    }

    // ==========================================
    // OBTENER POR ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        Color color = colorRepository
                .findById(id)
                .orElse(null);

        if (color == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Color no encontrado",
                            "id", id
                    ));
        }

        return ResponseEntity.ok(color);
    }

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Color color) {

        try {

            // Validar nombre
            if (color.getNombre() == null ||
                    color.getNombre().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "El nombre del color es obligatorio"
                        ));
            }

            // Validar código hexadecimal
            if (color.getCodigoHex() == null ||
                    !HEX_PATTERN.matcher(
                            color.getCodigoHex().trim()
                    ).matches()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "El código hexadecimal debe tener el formato #RRGGBB"
                        ));
            }

            color.setNombre(color.getNombre().trim());
            color.setCodigoHex(
                    color.getCodigoHex().trim().toUpperCase()
            );

            Color nuevoColor =
                    colorRepository.save(color);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevoColor);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se pudo crear el color porque ya existe o viola una restricción"
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

    // ==========================================
    // MODIFICAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Color color
    ) {

        Color colorExistente =
                colorRepository.findById(id).orElse(null);

        if (colorExistente == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Color no encontrado",
                            "id", id
                    ));
        }

        try {

            // Validar nombre
            if (color.getNombre() == null ||
                    color.getNombre().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "El nombre del color es obligatorio"
                        ));
            }

            // Validar código hexadecimal
            if (color.getCodigoHex() == null ||
                    !HEX_PATTERN.matcher(
                            color.getCodigoHex().trim()
                    ).matches()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "El código hexadecimal debe tener el formato #RRGGBB"
                        ));
            }

            colorExistente.setNombre(
                    color.getNombre().trim()
            );

            colorExistente.setCodigoHex(
                    color.getCodigoHex().trim().toUpperCase()
            );

            colorExistente.setActivo(
                    color.isActivo()
            );

            Color actualizado =
                    colorRepository.save(colorExistente);

            return ResponseEntity.ok(actualizado);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se pudo actualizar el color porque existe una restricción en la base de datos"
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

    // ==========================================
    // ELIMINAR
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Color color = colorRepository
                .findById(id)
                .orElse(null);

        if (color == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Color no encontrado",
                            "id", id
                    ));
        }

        try {

            colorRepository.delete(color);

            return ResponseEntity.noContent().build();

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se puede eliminar el color porque tiene variantes relacionadas"
                    ));
        }
    }
}