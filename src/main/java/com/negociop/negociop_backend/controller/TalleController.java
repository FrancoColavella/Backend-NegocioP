package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Talle;
import com.negociop.negociop_backend.repository.TalleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talles")
@CrossOrigin(origins = "*")
public class TalleController {

    private final TalleRepository talleRepository;

    public TalleController(TalleRepository talleRepository) {
        this.talleRepository = talleRepository;
    }

    @GetMapping
    public List<Talle> obtenerTodos() {
        return talleRepository.findAll();
    }

    @PostMapping
    public Talle crear(@RequestBody Talle talle) {
        return talleRepository.save(talle);
    }

    @PutMapping("/{id}")
    public Talle actualizar(
            @PathVariable Long id,
            @RequestBody Talle talleActualizado) {

        Talle talle = talleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Talle no encontrado"));

        talle.setNombre(talleActualizado.getNombre());
        talle.setActivo(talleActualizado.isActivo());

        return talleRepository.save(talle);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {

        Talle talle = talleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Talle no encontrado"));

        talle.setActivo(false);

        talleRepository.save(talle);
    }
}