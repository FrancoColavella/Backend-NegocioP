package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {

    List<ProductoImagen> findByProductoIdOrderByOrdenAsc(Long productoId);

    List<ProductoImagen> findByProductoIdAndColorIdOrderByOrdenAsc(
            Long productoId,
            Long colorId
    );
}