package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProveedorVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implementación JDBC de {@link ProveedorDAO} para persistir proveedores en MySQL. */
@Repository
public class ProveedorDAOImpl implements ProveedorDAO {

    private static final String SELECT_COLUMNS = "id_proveedor, nombre, telefono, correo, direccion, estado";
    private static final RowMapper<ProveedorVO> PROVEEDOR_ROW_MAPPER = (resultSet, rowNumber) ->
            new ProveedorVO(resultSet.getInt("id_proveedor"), resultSet.getString("nombre"),
                    resultSet.getString("telefono"), resultSet.getString("correo"),
                    resultSet.getString("direccion"), resultSet.getBoolean("estado"));

    private final JdbcTemplate jdbcTemplate;

    public ProveedorDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProveedorVO save(ProveedorVO proveedor) {
        String sql = "INSERT INTO proveedor (nombre, telefono, correo, direccion, estado) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setProveedorParameters(statement, proveedor);
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            proveedor.setIdProveedor(generatedId.intValue());
        }
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(true);
        }
        return proveedor;
    }

    @Override
    public ProveedorVO update(ProveedorVO proveedor) {
        if (proveedor.getIdProveedor() == null) {
            throw new IllegalArgumentException("El id del proveedor es obligatorio");
        }

        String sql = "UPDATE proveedor SET nombre = ?, telefono = ?, correo = ?, direccion = ?, estado = ? "
                + "WHERE id_proveedor = ?";
        int updatedRows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            setProveedorParameters(statement, proveedor);
            statement.setInt(6, Objects.requireNonNull(proveedor.getIdProveedor()));
            return statement;
        });
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No existe el proveedor con id " + proveedor.getIdProveedor());
        }
        return proveedor;
    }

    @Override
    public boolean deleteById(Integer idProveedor) {
        return jdbcTemplate.update("DELETE FROM proveedor WHERE id_proveedor = ?", idProveedor) > 0;
    }

    @Override
    public Optional<ProveedorVO> findById(Integer idProveedor) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM proveedor WHERE id_proveedor = ?",
                PROVEEDOR_ROW_MAPPER, idProveedor).stream().findFirst();
    }

    @Override
    public List<ProveedorVO> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM proveedor ORDER BY id_proveedor",
                PROVEEDOR_ROW_MAPPER);
    }

    @Override
    public List<ProveedorVO> findByEstado(Boolean estado) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM proveedor WHERE estado = ?",
                PROVEEDOR_ROW_MAPPER, estado);
    }

    @Override
    public List<ProveedorVO> findByNombreContaining(String nombre) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM proveedor WHERE LOWER(nombre) LIKE LOWER(?)",
                PROVEEDOR_ROW_MAPPER, "%" + nombre + "%");
    }

    private void setProveedorParameters(PreparedStatement statement, ProveedorVO proveedor)
            throws java.sql.SQLException {
        statement.setString(1, proveedor.getNombre().trim());
        statement.setString(2, proveedor.getTelefono());
        statement.setString(3, proveedor.getCorreo());
        statement.setString(4, proveedor.getDireccion());
        statement.setBoolean(5, proveedor.getEstado() == null || proveedor.getEstado());
    }
}
