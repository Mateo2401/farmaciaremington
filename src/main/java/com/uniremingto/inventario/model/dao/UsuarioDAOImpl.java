package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implementación JDBC de {@link UsuarioDAO} para persistir usuarios en MySQL. */
@Repository
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SELECT_COLUMNS = "id_usuario, nombre, correo, contrasena, rol, estado, fecha_registro";
    private static final RowMapper<UsuarioVO> USUARIO_ROW_MAPPER = (resultSet, rowNumber) -> {
        Timestamp fechaRegistro = resultSet.getTimestamp("fecha_registro");
        return new UsuarioVO(resultSet.getInt("id_usuario"), resultSet.getString("nombre"),
                resultSet.getString("correo"), resultSet.getString("contrasena"), resultSet.getString("rol"),
                resultSet.getBoolean("estado"), fechaRegistro == null ? null : fechaRegistro.toLocalDateTime());
    };

    private final JdbcTemplate jdbcTemplate;

    public UsuarioDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UsuarioVO save(UsuarioVO usuario) {
        String sql = "INSERT INTO usuario (nombre, correo, contrasena, rol, estado) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setUsuarioParameters(statement, usuario);
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            usuario.setIdUsuario(generatedId.intValue());
            findById(usuario.getIdUsuario()).ifPresent(guardado -> usuario.setFechaRegistro(guardado.getFechaRegistro()));
        }
        return usuario;
    }

    @Override
    public UsuarioVO update(UsuarioVO usuario) {
        if (usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("El id del usuario es obligatorio");
        }

        String sql = "UPDATE usuario SET nombre = ?, correo = ?, contrasena = ?, rol = ?, estado = ? "
                + "WHERE id_usuario = ?";
        int updatedRows = jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            setUsuarioParameters(statement, usuario);
            statement.setInt(6, Objects.requireNonNull(usuario.getIdUsuario()));
            return statement;
        });
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No existe el usuario con id " + usuario.getIdUsuario());
        }
        return usuario;
    }

    @Override
    public boolean deleteById(Integer idUsuario) {
        return jdbcTemplate.update("DELETE FROM usuario WHERE id_usuario = ?", idUsuario) > 0;
    }

    @Override
    public Optional<UsuarioVO> findById(Integer idUsuario) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM usuario WHERE id_usuario = ?",
                USUARIO_ROW_MAPPER, idUsuario).stream().findFirst();
    }

    @Override
    public List<UsuarioVO> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM usuario ORDER BY nombre",
                USUARIO_ROW_MAPPER);
    }

    @Override
    public Optional<UsuarioVO> findByCorreo(String correo) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM usuario WHERE LOWER(correo) = LOWER(?)",
                USUARIO_ROW_MAPPER, correo).stream().findFirst();
    }

    @Override
    public List<UsuarioVO> findByRol(String rol) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM usuario WHERE rol = ?",
                USUARIO_ROW_MAPPER, rol);
    }

    @Override
    public List<UsuarioVO> findByEstado(Boolean estado) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM usuario WHERE estado = ?",
                USUARIO_ROW_MAPPER, estado);
    }

    private void setUsuarioParameters(PreparedStatement statement, UsuarioVO usuario) throws java.sql.SQLException {
        statement.setString(1, usuario.getNombre());
        statement.setString(2, usuario.getCorreo());
        statement.setString(3, usuario.getContrasena());
        statement.setString(4, usuario.getRol());
        statement.setBoolean(5, usuario.getEstado() == null || usuario.getEstado());
    }
}
