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

    /**
     * Registra un movimiento de stock.
     *
     * IMPORTANTE:
     * Este método solamente registra el movimiento.
     * No modifica el stock de la variante.
     *
     * La modificación del stock se realizará desde los
     * servicios correspondientes y este servicio dejará
     * constancia del movimiento.
     */
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

        if (tipo == null ||
                tipo.trim().isEmpty()) {

            throw new RuntimeException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        if (cantidad == null ||
                cantidad <= 0) {

            throw new RuntimeException(
                    "La cantidad debe ser mayor a cero"
            );
        }

        if (stockAnterior == null ||
                stockAnterior < 0) {

            throw new RuntimeException(
                    "El stock anterior no es válido"
            );
        }

        if (stockPosterior == null ||
                stockPosterior < 0) {

            throw new RuntimeException(
                    "El stock posterior no es válido"
            );
        }

        if (motivo == null ||
                motivo.trim().isEmpty()) {

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

    /**
     * Obtiene todo el historial de movimientos.
     */
    public List<MovimientoStock> obtenerTodos() {

        return movimientoStockRepository
                .findAllByOrderByFechaDesc();
    }

    /**
     * Obtiene el historial de una variante específica.
     */
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
