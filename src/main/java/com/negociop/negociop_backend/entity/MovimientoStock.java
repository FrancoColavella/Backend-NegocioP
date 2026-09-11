package com.negociop.negociop_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variante_id", nullable = false)
    @JsonIgnoreProperties({
            "producto",
            "talle",
            "color"
    })
    private ProductoVariante variante;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer stockAnterior;

    @Column(nullable = false)
    private Integer stockPosterior;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public MovimientoStock() {
    }

    public Long getId() {
        return id;
    }

    public ProductoVariante getVariante() {
        return variante;
    }

    public void setVariante(ProductoVariante variante) {
        this.variante = variante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockPosterior() {
        return stockPosterior;
    }

    public void setStockPosterior(Integer stockPosterior) {
        this.stockPosterior = stockPosterior;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
