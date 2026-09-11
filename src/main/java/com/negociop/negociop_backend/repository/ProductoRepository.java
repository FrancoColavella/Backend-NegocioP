package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
