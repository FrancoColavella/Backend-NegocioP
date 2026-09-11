package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Pedido;
import com.negociop.negociop_backend.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(
            PedidoService pedidoService
    ) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(
            @RequestBody Pedido pedido
    ) {

        try {

            Pedido pedidoCreado =
                    pedidoService.crearPedido(
                            pedido
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(pedidoCreado);

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

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedido(
            @PathVariable Long id
    ) {

        try {

            Pedido pedido =
                    pedidoService.obtenerPedido(
                            id
                    );

            return ResponseEntity.ok(
                    pedido
            );

        } catch (RuntimeException e) {

            Map<String, String> respuesta =
                    new HashMap<>();

            respuesta.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(respuesta);
        }
    }
}
