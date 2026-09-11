package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoDetalleRepository
        extends JpaRepository<PedidoDetalle, Long> {
}
