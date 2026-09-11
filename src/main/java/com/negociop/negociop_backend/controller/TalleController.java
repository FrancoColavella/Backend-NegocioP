package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Talle;
import com.negociop.negociop_backend.repository.TalleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/talles")
@CrossOrigin(origins = "*")
public class TalleController {

    private final TalleRepository talleRepository;

    public TalleController(TalleRepository talleRepository) {
        this.talleRepository = talleRepository;
    }

    // ==========================================
    // OBTENER TODOS
    // ==========================================

    @GetMapping
    public List<Talle> obtenerTodos() {
        return talleRepository.findAll();
    }

    // ==========================================
    // OBTENER POR ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        Talle talle = talleRepository.findById(id).orElse(null);

        if (talle == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Talle no encontrado",
                            "id", id
                    ));
        }

        return ResponseEntity.ok(talle);
    }

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Talle talle) {

        try {

            if (talle.getNombre() == null ||
                    talle.getNombre().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "El nombre del talle es obligatorio"
                        ));
            }

            talle.setNombre(talle.getNombre().trim());

            Talle nuevoTalle = talleRepository.save(talle);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevoTalle);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se pudo crear el talle porque ya existe o viola una restricción"
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
            @RequestBody Talle talle
    ) {

        Talle talleExistente =
                talleRepository.findById(id).orElse(null);

        if (talleExistente == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Talle no encontrado",
                            "id", id
                    ));
        }

        try {

            if (talle.getNombre() == null ||
                    talle.getNombre().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "El nombre del talle es obligatorio"
                        ));
            }

            talleExistente.setNombre(
                    talle.getNombre().trim()
            );

            talleExistente.setActivo(
                    talle.isActivo()
            );

            Talle actualizado =
                    talleRepository.save(talleExistente);

            return ResponseEntity.ok(actualizado);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se pudo actualizar el talle porque existe una restricción en la base de datos"
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

        Talle talle = talleRepository
                .findById(id)
                .orElse(null);

        if (talle == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Talle no encontrado",
                            "id", id
                    ));
        }

        try {

            talleRepository.delete(talle);

            return ResponseEntity.noContent().build();

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se puede eliminar el talle porque tiene variantes relacionadas"
                    ));
        }
    }
}