package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implementación JDBC de {@link MovimientoInventarioDAO} para persistir movimientos en MySQL. */
@Repository
public class MovimientoInventarioDAOImpl implements MovimientoInventarioDAO {

    private static final String SELECT_COLUMNS = "id_movimiento, id_producto, id_usuario, tipo, cantidad, fecha, descripcion";
    private static final RowMapper<MovimientoInventarioVO> MOVIMIENTO_ROW_MAPPER = (resultSet, rowNumber) -> {
        Timestamp fecha = resultSet.getTimestamp("fecha");
        return new MovimientoInventarioVO(resultSet.getInt("id_movimiento"), resultSet.getInt("id_producto"),
                resultSet.getInt("id_usuario"), resultSet.getString("tipo"), resultSet.getInt("cantidad"),
                fecha == null ? null : fecha.toLocalDateTime(), resultSet.getString("descripcion"));
    };

    private final JdbcTemplate jdbcTemplate;

    public MovimientoInventarioDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MovimientoInventarioVO save(MovimientoInventarioVO movimiento) {
        String sql = "INSERT INTO movimiento_inventario (id_producto, id_usuario, tipo, cantidad, descripcion) "
                + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMovimientoParameters(statement, movimiento);
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            movimiento.setIdMovimiento(generatedId.intValue());
            findById(movimiento.getIdMovimiento()).ifPresent(guardado -> movimiento.setFecha(guardado.getFecha()));
        }
        return movimiento;
    }

    @Override
    public MovimientoInventarioVO update(MovimientoInventarioVO movimiento) {
        if (movimiento.getIdMovimiento() == null) {
            throw new IllegalArgumentException("El id del movimiento es obligatorio");
        }

        String sql = "UPDATE movimiento_inventario SET id_producto = ?, id_usuario = ?, tipo = ?, "
                + "cantidad = ?, descripcion = ? WHERE id_movimiento = ?";
        int updatedRows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            setMovimientoParameters(statement, movimiento);
            statement.setInt(6, Objects.requireNonNull(movimiento.getIdMovimiento()));
            return statement;
        });
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No existe el movimiento con id " + movimiento.getIdMovimiento());
        }
        return movimiento;
    }

    @Override
    public boolean deleteById(Integer idMovimiento) {
        return jdbcTemplate.update("DELETE FROM movimiento_inventario WHERE id_movimiento = ?", idMovimiento) > 0;
    }

    @Override
    public Optional<MovimientoInventarioVO> findById(Integer idMovimiento) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM movimiento_inventario WHERE id_movimiento = ?",
                MOVIMIENTO_ROW_MAPPER, idMovimiento).stream().findFirst();
    }

    @Override
    public List<MovimientoInventarioVO> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM movimiento_inventario "
                + "ORDER BY fecha DESC, id_movimiento DESC", MOVIMIENTO_ROW_MAPPER);
    }

    @Override
    public List<MovimientoInventarioVO> findByProducto(Integer idProducto) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM movimiento_inventario WHERE id_producto = ?",
                MOVIMIENTO_ROW_MAPPER, idProducto);
    }

    @Override
    public List<MovimientoInventarioVO> findByUsuario(Integer idUsuario) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM movimiento_inventario WHERE id_usuario = ?",
                MOVIMIENTO_ROW_MAPPER, idUsuario);
    }

    @Override
    public List<MovimientoInventarioVO> findByTipo(String tipo) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM movimiento_inventario WHERE tipo = ?",
                MOVIMIENTO_ROW_MAPPER, tipo);
    }

    @Override
    public List<MovimientoInventarioVO> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM movimiento_inventario WHERE fecha BETWEEN ? AND ?",
                MOVIMIENTO_ROW_MAPPER, Timestamp.valueOf(fechaInicio), Timestamp.valueOf(fechaFin));
    }

    private void setMovimientoParameters(PreparedStatement statement, MovimientoInventarioVO movimiento)
            throws java.sql.SQLException {
        statement.setInt(1, movimiento.getIdProducto());
        statement.setInt(2, movimiento.getIdUsuario());
        statement.setString(3, movimiento.getTipo());
        statement.setInt(4, movimiento.getCantidad());
        statement.setString(5, movimiento.getDescripcion());
    }
}
