package com.negociop.negociop_backend.controller;

import com.negociop.negociop_backend.entity.Categoria;
import com.negociop.negociop_backend.entity.Producto;
import com.negociop.negociop_backend.repository.CategoriaRepository;
import com.negociop.negociop_backend.repository.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoController(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // ==========================================
    // OBTENER TODOS LOS PRODUCTOS
    // ==========================================

    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // ==========================================
    // OBTENER PRODUCTO POR ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElse(null);

        if (producto == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Producto no encontrado",
                            "id", id
                    ));
        }

        return ResponseEntity.ok(producto);
    }

    // ==========================================
    // CREAR PRODUCTO
    // ==========================================

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {

        try {

            // Validar nombre
            if (producto.getNombre() == null ||
                    producto.getNombre().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "El nombre del producto es obligatorio"
                        ));
            }

            // Validar precio
            if (producto.getPrecio() == null ||
                    producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "El precio debe ser mayor o igual a 0"
                        ));
            }

            // Validar categoría
            if (producto.getCategoria() == null ||
                    producto.getCategoria().getId() == null) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "Debe indicar una categoría"
                        ));
            }

            // Buscar categoría real en la BD
            Categoria categoria = categoriaRepository
                    .findById(producto.getCategoria().getId())
                    .orElse(null);

            if (categoria == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Categoría no encontrada",
                                "id", producto.getCategoria().getId()
                        ));
            }

            producto.setCategoria(categoria);

            Producto nuevoProducto =
                    productoRepository.save(producto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevoProducto);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se pudo crear el producto por una restricción de la base de datos"
                    ));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    // ==========================================
    // MODIFICAR PRODUCTO
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto
    ) {

        Producto productoExistente = productoRepository
                .findById(id)
                .orElse(null);

        if (productoExistente == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Producto no encontrado",
                            "id", id
                    ));
        }

        try {

            // Validar nombre
            if (producto.getNombre() == null ||
                    producto.getNombre().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "El nombre del producto es obligatorio"
                        ));
            }

            // Validar precio
            if (producto.getPrecio() == null ||
                    producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "El precio debe ser mayor o igual a 0"
                        ));
            }

            // Validar categoría
            if (producto.getCategoria() == null ||
                    producto.getCategoria().getId() == null) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "Debe indicar una categoría"
                        ));
            }

            // Buscar categoría real
            Categoria categoria = categoriaRepository
                    .findById(producto.getCategoria().getId())
                    .orElse(null);

            if (categoria == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Categoría no encontrada",
                                "id", producto.getCategoria().getId()
                        ));
            }

            // Actualizar solamente los campos permitidos
            productoExistente.setNombre(producto.getNombre().trim());
            productoExistente.setDescripcion(producto.getDescripcion());
            productoExistente.setPrecio(producto.getPrecio());
            productoExistente.setVisible(producto.isVisible());
            productoExistente.setDisponible(producto.isDisponible());
            productoExistente.setCategoria(categoria);

            Producto actualizado =
                    productoRepository.save(productoExistente);

            return ResponseEntity.ok(actualizado);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se pudo actualizar el producto por una restricción de la base de datos"
                    ));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    // ==========================================
    // ELIMINAR PRODUCTO
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElse(null);

        if (producto == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Producto no encontrado",
                            "id", id
                    ));
        }

        try {

            /*
             * Por ahora permitimos eliminar.
             *
             * Más adelante vamos a proteger esta operación
             * cuando el producto tenga pedidos históricos.
             */

            productoRepository.delete(producto);

            return ResponseEntity.noContent().build();

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "No se puede eliminar el producto porque tiene información relacionada"
                    ));
        }
    }
}