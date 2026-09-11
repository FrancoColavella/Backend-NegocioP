package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}