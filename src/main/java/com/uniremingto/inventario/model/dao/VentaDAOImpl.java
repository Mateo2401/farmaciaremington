package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.VentaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implementación JDBC de {@link VentaDAO} para persistir ventas en MySQL. */
@Repository
public class VentaDAOImpl implements VentaDAO {

    private static final String SELECT_COLUMNS = "id_venta, id_usuario, fecha_venta, total, metodo_pago, estado";
    private static final RowMapper<VentaVO> VENTA_ROW_MAPPER = (resultSet, rowNumber) -> {
        Timestamp fechaVenta = resultSet.getTimestamp("fecha_venta");
        return new VentaVO(resultSet.getInt("id_venta"), resultSet.getInt("id_usuario"),
                fechaVenta == null ? null : fechaVenta.toLocalDateTime(), resultSet.getBigDecimal("total"),
                resultSet.getString("metodo_pago"), resultSet.getString("estado"));
    };

    private final JdbcTemplate jdbcTemplate;

    public VentaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public VentaVO save(VentaVO venta) {
        String sql = "INSERT INTO venta (id_usuario, total, metodo_pago, estado) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            setSaleParameters(statement, venta);
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            venta.setIdVenta(generatedId.intValue());
        }
        if (venta.getEstado() == null || venta.getEstado().isBlank()) {
            venta.setEstado("COMPLETADA");
        }
        return venta;
    }

    @Override
    public VentaVO update(VentaVO venta) {
        if (venta.getIdVenta() == null) {
            throw new IllegalArgumentException("El id de la venta es obligatorio");
        }

        String sql = "UPDATE venta SET id_usuario = ?, total = ?, metodo_pago = ?, estado = ? WHERE id_venta = ?";
        int updatedRows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            setSaleParameters(statement, venta);
            statement.setInt(5, Objects.requireNonNull(venta.getIdVenta()));
            return statement;
        });
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No existe la venta con id " + venta.getIdVenta());
        }
        return venta;
    }

    @Override
    public boolean deleteById(Integer idVenta) {
        return jdbcTemplate.update("DELETE FROM venta WHERE id_venta = ?", idVenta) > 0;
    }

    @Override
    public Optional<VentaVO> findById(Integer idVenta) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM venta WHERE id_venta = ?",
                VENTA_ROW_MAPPER, idVenta).stream().findFirst();
    }

    @Override
    public List<VentaVO> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM venta ORDER BY fecha_venta DESC, id_venta DESC",
                VENTA_ROW_MAPPER);
    }

    @Override
    public List<VentaVO> findByUsuario(Integer idUsuario) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM venta WHERE id_usuario = ?",
                VENTA_ROW_MAPPER, idUsuario);
    }

    @Override
    public List<VentaVO> findByEstado(String estado) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM venta WHERE estado = ?",
                VENTA_ROW_MAPPER, estado);
    }

    @Override
    public List<VentaVO> findByMetodoPago(String metodoPago) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM venta WHERE metodo_pago = ?",
                VENTA_ROW_MAPPER, metodoPago);
    }

    @Override
    public List<VentaVO> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM venta WHERE fecha_venta BETWEEN ? AND ?",
                VENTA_ROW_MAPPER, Timestamp.valueOf(fechaInicio), Timestamp.valueOf(fechaFin));
    }

    private void setSaleParameters(PreparedStatement statement, VentaVO venta) throws java.sql.SQLException {
        if (venta.getIdUsuario() == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setInt(1, venta.getIdUsuario());
        }
        statement.setBigDecimal(2, venta.getTotal());
        statement.setString(3, venta.getMetodoPago());
        statement.setString(4, venta.getEstado() == null || venta.getEstado().isBlank()
                ? "COMPLETADA" : venta.getEstado());
    }
}
