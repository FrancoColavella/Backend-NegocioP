package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.MovimientoStock;
import com.negociop.negociop_backend.service.MovimientoStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.negociop.negociop_backend.entity.ProductoVariante;


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

    @PostMapping("/entrada-salida")
    public ResponseEntity<?> registrarEntradaSalida(
            @RequestBody Map<String, Object> datos
    ) {

        try {

            Object varianteObject =
                    datos.get("varianteId");

            Object cantidadObject =
                    datos.get("cantidad");

            Object tipoObject =
                    datos.get("tipo");

            Object motivoObject =
                    datos.get("motivo");

            if (varianteObject == null) {
                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "La variante es obligatoria"
                                )
                        );
            }

            if (cantidadObject == null) {
                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "La cantidad es obligatoria"
                                )
                        );
            }

            if (tipoObject == null) {
                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "El tipo es obligatorio"
                                )
                        );
            }

            if (motivoObject == null) {
                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "El motivo es obligatorio"
                                )
                        );
            }

            Long varianteId =
                    ((Number) varianteObject).longValue();

            Integer cantidad =
                    ((Number) cantidadObject).intValue();

            String tipo =
                    tipoObject.toString();

            String motivo =
                    motivoObject.toString();

            ProductoVariante variante =
                    movimientoStockService
                            .registrarEntradaSalida(
                                    varianteId,
                                    tipo,
                                    cantidad,
                                    motivo
                            );

            return ResponseEntity.ok(variante);

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
}
