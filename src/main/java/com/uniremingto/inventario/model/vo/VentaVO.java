package com.uniremingto.inventario.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Transporta los datos de una venta y su estado de pago. */
public class VentaVO {

    private Integer idVenta;
    private Integer idUsuario;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private String metodoPago;
    private String estado;

    public VentaVO() {
    }

    public VentaVO(Integer idVenta, Integer idUsuario, LocalDateTime fechaVenta, BigDecimal total,
                   String metodoPago, String estado) {
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
