package com.negociop.negociop_backend.service;

import com.negociop.negociop_backend.entity.Color;
import com.negociop.negociop_backend.entity.Producto;
import com.negociop.negociop_backend.entity.ProductoImagen;
import com.negociop.negociop_backend.repository.ColorRepository;
import com.negociop.negociop_backend.repository.ProductoImagenRepository;
import com.negociop.negociop_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoImagenService {

    private final ProductoImagenRepository productoImagenRepository;
    private final ProductoRepository productoRepository;
    private final ColorRepository colorRepository;

    public ProductoImagenService(
            ProductoImagenRepository productoImagenRepository,
            ProductoRepository productoRepository,
            ColorRepository colorRepository
    ) {
        this.productoImagenRepository = productoImagenRepository;
        this.productoRepository = productoRepository;
        this.colorRepository = colorRepository;
    }

    public List<ProductoImagen> obtenerPorProducto(Long productoId) {
        return productoImagenRepository.findByProductoIdOrderByOrdenAsc(productoId);
    }

    public List<ProductoImagen> obtenerPorProductoYColor(
            Long productoId,
            Long colorId
    ) {
        return productoImagenRepository
                .findByProductoIdAndColorIdOrderByOrdenAsc(
                        productoId,
                        colorId
                );
    }

    @Transactional
    public ProductoImagen crear(
            Long productoId,
            Long colorId,
            String url,
            Integer orden
    ) {
        if (productoId == null) {
            throw new RuntimeException("El producto es obligatorio");
        }

        if (colorId == null) {
            throw new RuntimeException("El color es obligatorio");
        }

        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("La URL de la imagen es obligatoria");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe el producto con ID: " + productoId
                        )
                );

        Color color = colorRepository.findById(colorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe el color con ID: " + colorId
                        )
                );

        ProductoImagen imagen = new ProductoImagen();

        imagen.setProducto(producto);
        imagen.setColor(color);
        imagen.setUrl(url.trim());
        imagen.setOrden(orden != null ? orden : 0);

        return productoImagenRepository.save(imagen);
    }

    @Transactional
    public ProductoImagen actualizar(
            Long id,
            Long colorId,
            String url,
            Integer orden
    ) {
        ProductoImagen imagen = productoImagenRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe la imagen con ID: " + id
                        )
                );

        if (colorId == null) {
            throw new RuntimeException("El color es obligatorio");
        }

        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("La URL de la imagen es obligatoria");
        }

        Color color = colorRepository.findById(colorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe el color con ID: " + colorId
                        )
                );

        imagen.setColor(color);
        imagen.setUrl(url.trim());
        imagen.setOrden(orden != null ? orden : 0);

        return productoImagenRepository.save(imagen);
    }

    @Transactional
    public void eliminar(Long id) {

        if (!productoImagenRepository.existsById(id)) {
            throw new RuntimeException(
                    "No existe la imagen con ID: " + id
            );
        }

        productoImagenRepository.deleteById(id);
    }
}