package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.MovimientoStock;
import com.negociop.negociop_backend.service.MovimientoStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos-stock")
@CrossOrigin(origins = "*")
public class MovimientoStockController {

    private final MovimientoStockService movimientoStockService;

    public MovimientoStockController(
            MovimientoStockService movimientoStockService
    ) {
        this.movimientoStockService =
                movimientoStockService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {

        try {

            List<MovimientoStock> movimientos =
                    movimientoStockService.obtenerTodos();

            return ResponseEntity.ok(movimientos);

        } catch (RuntimeException e) {

            Map<String, String> respuesta =
                    new HashMap<>();

            respuesta.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(respuesta);
        }
    }

    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<?> obtenerPorVariante(
            @PathVariable Long varianteId
    ) {

        try {

            List<MovimientoStock> movimientos =
                    movimientoStockService
                            .obtenerPorVariante(varianteId);

            return ResponseEntity.ok(movimientos);

        } catch (RuntimeException e) {

            Map<String, String> respuesta =
                    new HashMap<>();

            respuesta.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(respuesta);
        }
    }
}
