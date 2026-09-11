package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoStockRepository
        extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findByVarianteIdOrderByFechaDesc(Long varianteId);

    List<MovimientoStock> findAllByOrderByFechaDesc();
}
