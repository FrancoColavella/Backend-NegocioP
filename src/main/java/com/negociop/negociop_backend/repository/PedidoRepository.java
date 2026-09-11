package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}

