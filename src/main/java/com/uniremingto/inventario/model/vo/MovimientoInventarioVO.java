package com.uniremingto.inventario.model.vo;

import java.time.LocalDateTime;

/** Transporta los datos de un movimiento aplicado al inventario. */
public class MovimientoInventarioVO {

    private Integer idMovimiento;
    private Integer idProducto;
    private Integer idUsuario;
    private String tipo;
    private Integer cantidad;
    private LocalDateTime fecha;
    private String descripcion;

    public MovimientoInventarioVO() {
    }

    public MovimientoInventarioVO(Integer idMovimiento, Integer idProducto, Integer idUsuario, String tipo,
                                 Integer cantidad, LocalDateTime fecha, String descripcion) {
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
