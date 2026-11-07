package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProductoVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ProductoDAOImpl implements ProductoDAO {

    private static final String SELECT_COLUMNS = "id_producto, codigo, nombre, descripcion, id_categoria, "
            + "id_proveedor, precio_compra, precio_venta, stock, stock_minimo, fecha_vencimiento, estado";

    private static final RowMapper<ProductoVO> PRODUCTO_ROW_MAPPER = (resultSet, rowNumber) -> new ProductoVO(
            resultSet.getInt("id_producto"),
            resultSet.getString("codigo"),
            resultSet.getString("nombre"),
            resultSet.getString("descripcion"),
            resultSet.getInt("id_categoria"),
            resultSet.getObject("id_proveedor", Integer.class),
            resultSet.getBigDecimal("precio_compra"),
            resultSet.getBigDecimal("precio_venta"),
            resultSet.getInt("stock"),
            resultSet.getInt("stock_minimo"),
            Optional.ofNullable(resultSet.getDate("fecha_vencimiento"))
                    .map(Date::toLocalDate)
                    .orElse(null),
            resultSet.getBoolean("estado"));

    private final JdbcTemplate jdbcTemplate;

    public ProductoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProductoVO save(ProductoVO producto) {
        String sql = "INSERT INTO producto (codigo, nombre, descripcion, id_categoria, id_proveedor, "
                + "precio_compra, precio_venta, stock, stock_minimo, fecha_vencimiento, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setProductParameters(statement, producto, false);
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            producto.setIdProducto(generatedId.intValue());
        }
        return producto;
    }

    @Override
    public ProductoVO update(ProductoVO producto) {
        if (producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El id del producto es obligatorio");
        }

        String sql = "UPDATE producto SET codigo = ?, nombre = ?, descripcion = ?, id_categoria = ?, "
                + "id_proveedor = ?, precio_compra = ?, precio_venta = ?, stock = ?, stock_minimo = ?, "
                + "fecha_vencimiento = ?, estado = ? WHERE id_producto = ?";
        int updatedRows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            setProductParameters(statement, producto, true);
            return statement;
        });

        if (updatedRows == 0) {
            throw new IllegalArgumentException("No existe el producto con id " + producto.getIdProducto());
        }
        return producto;
    }

    @Override
    public boolean deleteById(Integer idProducto) {
        return jdbcTemplate.update("DELETE FROM producto WHERE id_producto = ?", idProducto) > 0;
    }

    @Override
    public Optional<ProductoVO> findById(Integer idProducto) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE id_producto = ?",
                PRODUCTO_ROW_MAPPER, idProducto).stream().findFirst();
    }

    @Override
    public List<ProductoVO> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto ORDER BY id_producto",
                PRODUCTO_ROW_MAPPER);
    }

    @Override
    public Optional<ProductoVO> findByCodigo(String codigo) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE LOWER(codigo) = LOWER(?)",
                PRODUCTO_ROW_MAPPER, codigo).stream().findFirst();
    }

    @Override
    public List<ProductoVO> findByNombreContaining(String nombre) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE LOWER(nombre) LIKE LOWER(?)",
                PRODUCTO_ROW_MAPPER, "%" + nombre + "%");
    }

    @Override
    public List<ProductoVO> findByCategoria(Integer idCategoria) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE id_categoria = ?",
                PRODUCTO_ROW_MAPPER, idCategoria);
    }

    @Override
    public List<ProductoVO> findByProveedor(Integer idProveedor) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE id_proveedor = ?",
                PRODUCTO_ROW_MAPPER, idProveedor);
    }

    @Override
    public List<ProductoVO> findByEstado(Boolean estado) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE estado = ?",
                PRODUCTO_ROW_MAPPER, estado);
    }

    @Override
    public List<ProductoVO> findByStockLessThan(Integer stock) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE stock < ?",
                PRODUCTO_ROW_MAPPER, stock);
    }

    @Override
    public List<ProductoVO> findLowStockProducts(Integer stockMinimo) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM producto WHERE stock <= ?",
                PRODUCTO_ROW_MAPPER, stockMinimo);
    }

    private void setProductParameters(PreparedStatement statement, ProductoVO producto, boolean includeId)
            throws java.sql.SQLException {
        statement.setString(1, producto.getCodigo());
        statement.setString(2, producto.getNombre());
        statement.setString(3, producto.getDescripcion());
        statement.setInt(4, producto.getIdCategoria());
        if (producto.getIdProveedor() == null) {
            statement.setNull(5, Types.INTEGER);
        } else {
            statement.setInt(5, producto.getIdProveedor());
        }
        statement.setBigDecimal(6, producto.getPrecioCompra());
        statement.setBigDecimal(7, producto.getPrecioVenta());
        statement.setInt(8, producto.getStock());
        statement.setInt(9, producto.getStockMinimo());
        if (producto.getFechaVencimiento() == null) {
            statement.setNull(10, Types.DATE);
        } else {
            statement.setDate(10, Date.valueOf(producto.getFechaVencimiento()));
        }
        if (producto.getEstado() == null) {
            statement.setNull(11, Types.BOOLEAN);
        } else {
            statement.setBoolean(11, producto.getEstado());
        }
        if (includeId) {
            statement.setInt(12, Objects.requireNonNull(producto.getIdProducto()));
        }
    }
}
