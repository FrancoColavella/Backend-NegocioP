package com.negociop.negociop_backend.service;

import com.negociop.negociop_backend.entity.Pedido;
import com.negociop.negociop_backend.entity.PedidoDetalle;
import com.negociop.negociop_backend.entity.Producto;
import com.negociop.negociop_backend.entity.ProductoVariante;
import com.negociop.negociop_backend.repository.PedidoRepository;
import com.negociop.negociop_backend.repository.ProductoRepository;
import com.negociop.negociop_backend.repository.ProductoVarianteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ProductoVarianteRepository productoVarianteRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoRepository productoRepository,
            ProductoVarianteRepository productoVarianteRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.productoVarianteRepository = productoVarianteRepository;
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {

        if (pedido.getDetalles() == null ||
                pedido.getDetalles().isEmpty()) {

            throw new RuntimeException(
                    "El pedido debe contener al menos un producto"
            );
        }

        BigDecimal subtotal =
                BigDecimal.ZERO;

        /*
         * Procesamos cada detalle recibido.
         */
        for (PedidoDetalle detalle : pedido.getDetalles()) {

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
             * Buscamos la variante real
             * en la base de datos.
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

            /*
             * Validamos stock real.
             */
            int stockActual =
                    variante.getStock() != null
                            ? variante.getStock()
                            : 0;

            int cantidadSolicitada =
                    detalle.getCantidad();

            if (stockActual < cantidadSolicitada) {

                throw new RuntimeException(
                        "Stock insuficiente para la variante "
                                + varianteId
                                + ". Stock disponible: "
                                + stockActual
                );
            }

            /*
             * Obtenemos el producto real asociado
             * a la variante.
             */
            Producto producto =
                    variante.getProducto();

            if (producto == null ||
                    producto.getId() == null) {

                throw new RuntimeException(
                        "La variante "
                                + varianteId
                                + " no tiene un producto válido"
                );
            }

            /*
             * Buscamos nuevamente el producto
             * para trabajar con los datos reales
             * de la base.
             */
            Producto productoReal =
                    productoRepository
                            .findById(producto.getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "No existe el producto con ID: "
                                                    + producto.getId()
                                    )
                            );

            /*
             * Validamos que el producto esté
             * disponible para la venta.
             */
            if (!productoReal.isDisponible()) {
                throw new RuntimeException(
                        "El producto "
                                + productoReal.getNombre()
                                + " no está disponible"
                );
            }

            /*
             * El precio SIEMPRE sale del backend.
             * Nunca confiamos en el precio enviado
             * por el navegador.
             */
            BigDecimal precioUnitario =
                    productoReal.getPrecio();

            BigDecimal subtotalDetalle =
                    precioUnitario.multiply(
                            BigDecimal.valueOf(
                                    cantidadSolicitada
                            )
                    );

            /*
             * Guardamos los datos reales
             * en el detalle.
             */
            detalle.setPedido(pedido);
            detalle.setProducto(productoReal);
            detalle.setVariante(variante);
            detalle.setPrecioUnitario(
                    precioUnitario
            );
            detalle.setSubtotal(
                    subtotalDetalle
            );

            subtotal =
                    subtotal.add(
                            subtotalDetalle
                    );

            /*
             * Descontamos el stock.
             */
            variante.setStock(
                    stockActual - cantidadSolicitada
            );
        }

        /*
         * Si no se informa costo de envío,
         * utilizamos cero.
         */
        BigDecimal costoEnvio =
                pedido.getCostoEnvio();

        if (costoEnvio == null) {
            costoEnvio =
                    BigDecimal.ZERO;
        }

        /*
         * Calculamos el total en el backend.
         */
        BigDecimal total =
                subtotal.add(costoEnvio);

        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(total);

        /*
         * Estado inicial del pedido.
         */
        pedido.setEstado("PENDIENTE");

        /*
         * Fecha generada por el backend.
         */
        pedido.setFecha(
                LocalDateTime.now()
        );

        /*
         * Guardamos el pedido.
         *
         * CascadeType.ALL en Pedido hará que
         * también se guarden los detalles.
         */
        return pedidoRepository.save(pedido);
    }

    public Pedido obtenerPedido(Long id) {

        return pedidoRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe el pedido con ID: "
                                        + id
                        )
                );
    }
}
