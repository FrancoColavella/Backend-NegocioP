package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.ProductoImagen;
import com.negociop.negociop_backend.service.ProductoImagenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductoImagenController {

    private final ProductoImagenService productoImagenService;

    public ProductoImagenController(
            ProductoImagenService productoImagenService
    ) {
        this.productoImagenService = productoImagenService;
    }

    // =========================================================
    // OBTENER TODAS LAS IMÁGENES DE UN PRODUCTO
    // =========================================================

    @GetMapping("/productos/{productoId}/imagenes")
    public ResponseEntity<List<ProductoImagen>> obtenerPorProducto(
            @PathVariable Long productoId
    ) {
        return ResponseEntity.ok(
                productoImagenService.obtenerPorProducto(productoId)
        );
    }

    // =========================================================
    // OBTENER IMÁGENES DE UN PRODUCTO POR COLOR
    // =========================================================

    @GetMapping("/productos/{productoId}/imagenes/color/{colorId}")
    public ResponseEntity<List<ProductoImagen>> obtenerPorProductoYColor(
            @PathVariable Long productoId,
            @PathVariable Long colorId
    ) {
        return ResponseEntity.ok(
                productoImagenService.obtenerPorProductoYColor(
                        productoId,
                        colorId
                )
        );
    }

    // =========================================================
    // CREAR IMAGEN
    // =========================================================

    @PostMapping("/productos/{productoId}/imagenes")
    public ResponseEntity<ProductoImagen> crear(
            @PathVariable Long productoId,
            @RequestBody Map<String, Object> body
    ) {
        Long colorId = obtenerLong(body.get("colorId"));

        String url = body.get("url") != null
                ? body.get("url").toString()
                : null;

        Integer orden = obtenerInteger(body.get("orden"));

        ProductoImagen imagen = productoImagenService.crear(
                productoId,
                colorId,
                url,
                orden
        );

        return ResponseEntity.ok(imagen);
    }

    // =========================================================
    // ACTUALIZAR IMAGEN
    // =========================================================

    @PutMapping("/imagenes/{id}")
    public ResponseEntity<ProductoImagen> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Long colorId = obtenerLong(body.get("colorId"));

        String url = body.get("url") != null
                ? body.get("url").toString()
                : null;

        Integer orden = obtenerInteger(body.get("orden"));

        ProductoImagen imagen = productoImagenService.actualizar(
                id,
                colorId,
                url,
                orden
        );

        return ResponseEntity.ok(imagen);
    }

    // =========================================================
    // ELIMINAR IMAGEN
    // =========================================================

    @DeleteMapping("/imagenes/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        productoImagenService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // CONVERSIÓN DE DATOS
    // =========================================================

    private Long obtenerLong(Object valor) {

        if (valor == null) {
            return null;
        }

        if (valor instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.parseLong(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer obtenerInteger(Object valor) {

        if (valor == null) {
            return null;
        }

        if (valor instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}