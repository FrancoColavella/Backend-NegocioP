package com.negociop.negociop_backend.repository;

import com.negociop.negociop_backend.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Long> {
}