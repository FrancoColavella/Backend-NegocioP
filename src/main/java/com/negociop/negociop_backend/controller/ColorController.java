package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Color;
import com.negociop.negociop_backend.repository.ColorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colores")
@CrossOrigin(origins = "*")
public class ColorController {

    private final ColorRepository colorRepository;

    public ColorController(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @GetMapping
    public List<Color> obtenerTodos() {
        return colorRepository.findAll();
    }

    @PostMapping
    public Color crear(@RequestBody Color color) {
        return colorRepository.save(color);
    }

    @PutMapping("/{id}")
    public Color actualizar(
            @PathVariable Long id,
            @RequestBody Color colorActualizado) {

        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado"));

        color.setNombre(colorActualizado.getNombre());
        color.setCodigoHex(colorActualizado.getCodigoHex());
        color.setActivo(colorActualizado.isActivo());

        return colorRepository.save(color);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {

        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado"));

        color.setActivo(false);

        colorRepository.save(color);
    }
}