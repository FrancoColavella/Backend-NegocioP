package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.ProductoVariante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoVarianteRepository extends JpaRepository<ProductoVariante, Long> {

    Optional<ProductoVariante> findByProductoIdAndTalleIdAndColorId(
            Long productoId,
            Long talleId,
            Long colorId
    );

    List<ProductoVariante> findByProductoId(Long productoId);
}