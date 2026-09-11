package com.negociop.negociop_backend.service;

import com.negociop.negociop_backend.entity.Pedido;
import com.negociop.negociop_backend.entity.PedidoDetalle;
import com.negociop.negociop_backend.entity.Producto;
import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.repository.PedidoRepository;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoVarianteRepository productoVarianteRepository;
    private final MovimientoStockService movimientoStockService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoVarianteRepository productoVarianteRepository,
            MovimientoStockService movimientoStockService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.productoVarianteRepository = productoVarianteRepository;
        this.movimientoStockService = movimientoStockService;
    }

    /**
     * Crea un pedido.
     *
     * Flujo:
     *
     * 1. Valida el pedido.
     * 2. Verifica stock.
     * 3. Obtiene precios reales de la BD.
     * 4. Calcula subtotales.
     * 5. Descuenta stock.
     * 6. Asocia producto y variante reales al detalle.
     * 7. Guarda el pedido.
     * 8. Registra los movimientos VENTA.
     *
     * Todo ocurre dentro de una única transacción.
     */
    @Transactional
    public Pedido crearPedido(Pedido pedido) {

        if (pedido == null) {
            throw new RuntimeException(
                    "El pedido es obligatorio"
            );
        }

        if (pedido.getDetalles() == null ||
                pedido.getDetalles().isEmpty()) {

            throw new RuntimeException(
                    "El pedido debe contener al menos un producto"
            );
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        /*
         * Procesamos todos los detalles.
         */
        for (PedidoDetalle detalle : pedido.getDetalles()) {

            if (detalle == null) {
                throw new RuntimeException(
                        "El detalle del pedido no es válido"
                );
            }

            if (detalle.getCantidad() == null ||
                    detalle.getCantidad() <= 0) {

                throw new RuntimeException(
                        "La cantidad debe ser mayor a cero"
                );
            }

            if (detalle.getVariante() == null ||
                    detalle.getVariante().getId() == null) {

                throw new RuntimeException(
                        "Cada detalle debe indicar una variante"
                );
            }

            Long varianteId =
                    detalle.getVariante().getId();

            /*
             * Obtenemos la variante real desde la BD.
             */
            ProductoVariante variante =
                    productoVarianteRepository
                            .findById(varianteId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "No existe la variante con ID: "
                                                    + varianteId
                                    )
                            );

            Integer stockAnterior =
                    variante.getStock();

            if (stockAnterior == null ||
                    stockAnterior < 0) {

                throw new RuntimeException(
                        "El stock de la variante no es válido"
                );
            }

            int cantidad =
                    detalle.getCantidad();

            /*
             * Verificamos stock.
             */
            if (stockAnterior < cantidad) {

                throw new RuntimeException(
                        "Stock insuficiente para la variante ID: "
                                + varianteId
                                + ". Stock disponible: "
                                + stockAnterior
                                + ", cantidad solicitada: "
                                + cantidad
                );
            }

            /*
             * Obtenemos el producto real desde
             * la variante consultada en la BD.
             */
            Producto producto =
                    variante.getProducto();

            if (producto == null) {

                throw new RuntimeException(
                        "La variante no tiene un producto asociado"
                );
            }

            /*
             * El producto real viene de la BD.
             */
            Producto productoReal =
                    producto;

            /*
             * Verificamos disponibilidad.
             */
            if (!productoReal.isDisponible()) {

                throw new RuntimeException(
                        "El producto no está disponible: "
                                + productoReal.getNombre()
                );
            }

            /*
             * Obtenemos el precio real de la BD.
             */
            BigDecimal precio =
                    productoReal.getPrecio();

            if (precio == null ||
                    precio.compareTo(BigDecimal.ZERO) < 0) {

                throw new RuntimeException(
                        "El precio del producto no es válido: "
                                + productoReal.getNombre()
                );
            }

            /*
             * IMPORTANTE:
             *
             * El precio enviado por el frontend
             * NO se utiliza.
             *
             * Siempre utilizamos el precio de la BD.
             */
            detalle.setPrecioUnitario(precio);

            BigDecimal subtotalDetalle =
                    precio.multiply(
                            BigDecimal.valueOf(cantidad)
                    );

            detalle.setSubtotal(subtotalDetalle);

            subtotal =
                    subtotal.add(subtotalDetalle);

            /*
             * Descontamos el stock.
             */
            int stockPosterior =
                    stockAnterior - cantidad;

            variante.setStock(stockPosterior);

            productoVarianteRepository.save(variante);

            /*
             * IMPORTANTE:
             *
             * Asociamos al detalle las entidades REALES
             * obtenidas desde la base de datos.
             *
             * Esto evita que Hibernate intente guardar
             * un producto nulo o una entidad transitoria.
             */
            detalle.setProducto(productoReal);
            detalle.setVariante(variante);

            /*
             * Asociamos el detalle al pedido.
             */
            detalle.setPedido(pedido);
        }

        /*
         * Costo de envío.
         */
        BigDecimal costoEnvio =
                pedido.getCostoEnvio();

        if (costoEnvio == null) {
            costoEnvio = BigDecimal.ZERO;
        }

        if (costoEnvio.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "El costo de envío no puede ser negativo"
            );
        }

        /*
         * Calculamos total.
         */
        BigDecimal total =
                subtotal.add(costoEnvio);

        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(total);

        /*
         * Estado inicial.
         */
        pedido.setEstado("PENDIENTE");

        /*
         * Fecha de creación.
         */
        pedido.setFecha(LocalDateTime.now());

        /*
         * Guardamos el pedido.
         *
         * En este momento Hibernate genera el ID.
         */
        Pedido pedidoGuardado =
                pedidoRepository.save(pedido);

        /*
         * Ahora ya tenemos el ID real del pedido.
         *
         * Registramos los movimientos VENTA.
         */
        for (PedidoDetalle detalle :
                pedidoGuardado.getDetalles()) {

            ProductoVariante variante =
                    detalle.getVariante();

            if (variante == null ||
                    variante.getId() == null) {

                throw new RuntimeException(
                        "El detalle no tiene una variante válida"
                );
            }

            int cantidad =
                    detalle.getCantidad();

            int stockPosterior =
                    variante.getStock();

            int stockAnterior =
                    stockPosterior + cantidad;

            /*
             * Registramos el movimiento de venta.
             */
            movimientoStockService.registrarMovimiento(
                    variante.getId(),
                    "VENTA",
                    cantidad,
                    stockAnterior,
                    stockPosterior,
                    "Pedido #" + pedidoGuardado.getId()
            );
        }

        return pedidoGuardado;
    }

    /**
     * Obtiene un pedido por ID.
     */
    public Pedido obtenerPedido(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "El ID del pedido es obligatorio"
            );
        }

        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe el pedido con ID: "
                                        + id
                        )
                );
    }

    /**
     * Obtiene todos los pedidos.
     */
    public List<Pedido> obtenerTodosLosPedidos() {

        return pedidoRepository.findAll();
    }

    /**
     * Cambia el estado de un pedido.
     *
     * Estados permitidos:
     *
     * PENDIENTE
     * CONFIRMADO
     * PREPARANDO
     * ENVIADO
     * ENTREGADO
     * CANCELADO
     *
     * Cuando pasa a CANCELADO:
     *
     * - Devuelve el stock.
     * - Registra DEVOLUCION.
     */
    @Transactional
    public Pedido cambiarEstado(
            Long id,
            String nuevoEstado
    ) {

        if (id == null) {
            throw new RuntimeException(
                    "El ID del pedido es obligatorio"
            );
        }

        if (nuevoEstado == null ||
                nuevoEstado.trim().isEmpty()) {

            throw new RuntimeException(
                    "El nuevo estado es obligatorio"
            );
        }

        String estadoNormalizado =
                nuevoEstado.trim().toUpperCase();

        List<String> estadosValidos = List.of(
                "PENDIENTE",
                "CONFIRMADO",
                "PREPARANDO",
                "ENVIADO",
                "ENTREGADO",
                "CANCELADO"
        );

        if (!estadosValidos.contains(estadoNormalizado)) {

            throw new RuntimeException(
                    "Estado inválido: "
                            + estadoNormalizado
            );
        }

        Pedido pedido =
                pedidoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe el pedido con ID: "
                                                + id
                                )
                        );

        String estadoActual =
                pedido.getEstado();

        /*
         * Si ya está cancelado:
         *
         * CANCELADO -> CANCELADO
         * simplemente devolvemos el pedido.
         */
        if ("CANCELADO".equals(estadoActual)) {

            if ("CANCELADO".equals(estadoNormalizado)) {
                return pedido;
            }

            throw new RuntimeException(
                    "Un pedido cancelado no puede volver a activarse"
            );
        }

        /*
         * Si pasa a CANCELADO,
         * devolvemos el stock.
         */
        if ("CANCELADO".equals(estadoNormalizado)) {

            if (pedido.getDetalles() == null ||
                    pedido.getDetalles().isEmpty()) {

                throw new RuntimeException(
                        "El pedido no contiene detalles para devolver stock"
                );
            }

            for (PedidoDetalle detalle :
                    pedido.getDetalles()) {

                if (detalle == null) {
                    throw new RuntimeException(
                            "El detalle del pedido no es válido"
                    );
                }

                if (detalle.getCantidad() == null ||
                        detalle.getCantidad() <= 0) {

                    throw new RuntimeException(
                            "La cantidad del detalle no es válida"
                    );
                }

                if (detalle.getVariante() == null ||
                        detalle.getVariante().getId() == null) {

                    throw new RuntimeException(
                            "El detalle no tiene una variante válida"
                    );
                }

                Long varianteId =
                        detalle.getVariante().getId();

                /*
                 * Obtenemos la variante real.
                 */
                ProductoVariante variante =
                        productoVarianteRepository
                                .findById(varianteId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "No existe la variante con ID: "
                                                        + varianteId
                                        )
                                );

                Integer stockAnterior =
                        variante.getStock();

                if (stockAnterior == null ||
                        stockAnterior < 0) {

                    throw new RuntimeException(
                            "El stock de la variante no es válido"
                    );
                }

                int cantidad =
                        detalle.getCantidad();

                /*
                 * Devolvemos el stock.
                 */
                int stockPosterior =
                        stockAnterior + cantidad;

                variante.setStock(stockPosterior);

                productoVarianteRepository.save(variante);

                /*
                 * Registramos la devolución.
                 */
                movimientoStockService.registrarMovimiento(
                        varianteId,
                        "DEVOLUCION",
                        cantidad,
                        stockAnterior,
                        stockPosterior,
                        "Cancelación pedido #" + id
                );
            }
        }

        pedido.setEstado(estadoNormalizado);

        return pedidoRepository.save(pedido);
    }
}