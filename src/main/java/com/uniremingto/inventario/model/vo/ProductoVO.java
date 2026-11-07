package com.uniremingto.inventario.model.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductoVO {

    private Integer idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer idCategoria;
    private Integer idProveedor;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private LocalDate fechaVencimiento;
    private Boolean estado;

    public ProductoVO() {
    }

    public ProductoVO(Integer idProducto, String codigo, String nombre, String descripcion, Integer idCategoria,
                      Integer idProveedor, BigDecimal precioCompra, BigDecimal precioVenta, Integer stock,
                      Integer stockMinimo, LocalDate fechaVencimiento, Boolean estado) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
