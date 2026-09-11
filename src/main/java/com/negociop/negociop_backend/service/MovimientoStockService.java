package com.negociop.negociop_backend.service;

import com.negociop.negociop_backend.entity.MovimientoStock;
import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.repository.MovimientoStockRepository;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;
    private final ProductoVarianteRepository productoVarianteRepository;

    public MovimientoStockService(
            MovimientoStockRepository movimientoStockRepository,
            ProductoVarianteRepository productoVarianteRepository
    ) {
        this.movimientoStockRepository = movimientoStockRepository;
        this.productoVarianteRepository = productoVarianteRepository;
    }

    // =========================================================
    // REGISTRAR MOVIMIENTO
    // =========================================================

    @Transactional
    public MovimientoStock registrarMovimiento(
            Long varianteId,
            String tipo,
            Integer cantidad,
            Integer stockAnterior,
            Integer stockPosterior,
            String motivo
    ) {

        if (varianteId == null) {
            throw new RuntimeException(
                    "La variante es obligatoria"
            );
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new RuntimeException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException(
                    "La cantidad debe ser mayor a cero"
            );
        }

        if (stockAnterior == null || stockAnterior < 0) {
            throw new RuntimeException(
                    "El stock anterior no es válido"
            );
        }

        if (stockPosterior == null || stockPosterior < 0) {
            throw new RuntimeException(
                    "El stock posterior no es válido"
            );
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new RuntimeException(
                    "El motivo es obligatorio"
            );
        }

        String tipoNormalizado =
                tipo.trim().toUpperCase();

        List<String> tiposValidos = List.of(
                "ENTRADA",
                "SALIDA",
                "AJUSTE",
                "VENTA",
                "DEVOLUCION"
        );

        if (!tiposValidos.contains(tipoNormalizado)) {
            throw new RuntimeException(
                    "Tipo de movimiento inválido: "
                            + tipoNormalizado
            );
        }

        ProductoVariante variante =
                productoVarianteRepository.findById(varianteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe la variante con ID: "
                                                + varianteId
                                )
                        );

        MovimientoStock movimiento =
                new MovimientoStock();

        movimiento.setVariante(variante);
        movimiento.setTipo(tipoNormalizado);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        movimiento.setMotivo(motivo.trim());
        movimiento.setFecha(LocalDateTime.now());

        return movimientoStockRepository.save(movimiento);
    }

    // =========================================================
    // AJUSTE MANUAL
    // =========================================================

    @Transactional
    public ProductoVariante actualizarStockManual(
            Long varianteId,
            Integer nuevoStock
    ) {

        if (varianteId == null) {
            throw new RuntimeException(
                    "La variante es obligatoria"
            );
        }

        if (nuevoStock == null || nuevoStock < 0) {
            throw new RuntimeException(
                    "El stock debe ser mayor o igual a cero"
            );
        }

        ProductoVariante variante =
                productoVarianteRepository.findById(varianteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe la variante con ID: "
                                                + varianteId
                                )
                        );

        int stockAnterior =
                variante.getStock();

        int stockPosterior =
                nuevoStock;

        if (stockAnterior == stockPosterior) {
            return variante;
        }

        int cantidad =
                Math.abs(
                        stockPosterior -
                                stockAnterior
                );

        variante.setStock(stockPosterior);

        ProductoVariante actualizada =
                productoVarianteRepository.save(
                        variante
                );

        registrarMovimiento(
                varianteId,
                "AJUSTE",
                cantidad,
                stockAnterior,
                stockPosterior,
                "Ajuste manual de stock"
        );

        return actualizada;
    }

    // =========================================================
    // ENTRADA / SALIDA DE MERCADERÍA
    // =========================================================

    @Transactional
    public ProductoVariante registrarEntradaSalida(
            Long varianteId,
            String tipo,
            Integer cantidad,
            String motivo
    ) {

        if (varianteId == null) {
            throw new RuntimeException(
                    "La variante es obligatoria"
            );
        }

        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException(
                    "La cantidad debe ser mayor a cero"
            );
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new RuntimeException(
                    "El motivo es obligatorio"
            );
        }

        String tipoNormalizado =
                tipo.trim().toUpperCase();

        if (
                !tipoNormalizado.equals("ENTRADA") &&
                        !tipoNormalizado.equals("SALIDA")
        ) {
            throw new RuntimeException(
                    "El tipo debe ser ENTRADA o SALIDA"
            );
        }

        ProductoVariante variante =
                productoVarianteRepository.findById(varianteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe la variante con ID: "
                                                + varianteId
                                )
                        );

        int stockAnterior =
                variante.getStock();

        int stockPosterior;

        if (tipoNormalizado.equals("ENTRADA")) {

            stockPosterior =
                    stockAnterior + cantidad;

        } else {

            if (cantidad > stockAnterior) {
                throw new RuntimeException(
                        "No hay suficiente stock. " +
                                "Stock actual: " +
                                stockAnterior
                );
            }

            stockPosterior =
                    stockAnterior - cantidad;
        }

        variante.setStock(stockPosterior);

        ProductoVariante actualizada =
                productoVarianteRepository.save(
                        variante
                );

        registrarMovimiento(
                varianteId,
                tipoNormalizado,
                cantidad,
                stockAnterior,
                stockPosterior,
                motivo
        );

        return actualizada;
    }

    // =========================================================
    // HISTORIAL
    // =========================================================

    public List<MovimientoStock> obtenerTodos() {

        return movimientoStockRepository
                .findAllByOrderByFechaDesc();
    }

    public List<MovimientoStock> obtenerPorVariante(
            Long varianteId
    ) {

        if (varianteId == null) {
            throw new RuntimeException(
                    "La variante es obligatoria"
            );
        }

        return movimientoStockRepository
                .findByVarianteIdOrderByFechaDesc(
                        varianteId
                );
    }
}
