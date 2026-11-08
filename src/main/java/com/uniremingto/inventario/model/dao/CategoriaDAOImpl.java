package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.CategoriaVO;
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

/** Implementación JDBC de {@link CategoriaDAO} para persistir categorías en MySQL. */
@Repository
public class CategoriaDAOImpl implements CategoriaDAO {

    private static final String SELECT_COLUMNS = "id_categoria, nombre, descripcion";
    private static final RowMapper<CategoriaVO> CATEGORIA_ROW_MAPPER = (resultSet, rowNumber) ->
            new CategoriaVO(resultSet.getInt("id_categoria"), resultSet.getString("nombre"),
                    resultSet.getString("descripcion"));

    private final JdbcTemplate jdbcTemplate;

    public CategoriaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CategoriaVO save(CategoriaVO categoria) {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, categoria.getNombre());
            statement.setString(2, categoria.getDescripcion());
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            categoria.setIdCategoria(generatedId.intValue());
        }
        return categoria;
    }

    @Override
    public CategoriaVO update(CategoriaVO categoria) {
        if (categoria.getIdCategoria() == null) {
            throw new IllegalArgumentException("El id de la categoría es obligatorio");
        }

        int updatedRows = jdbcTemplate.update("UPDATE categoria SET nombre = ?, descripcion = ? "
                        + "WHERE id_categoria = ?",
                categoria.getNombre(), categoria.getDescripcion(),
                Objects.requireNonNull(categoria.getIdCategoria()));
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No existe la categoría con id " + categoria.getIdCategoria());
        }
        return categoria;
    }

    @Override
    public boolean deleteById(Integer idCategoria) {
        return jdbcTemplate.update("DELETE FROM categoria WHERE id_categoria = ?", idCategoria) > 0;
    }

    @Override
    public Optional<CategoriaVO> findById(Integer idCategoria) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM categoria WHERE id_categoria = ?",
                CATEGORIA_ROW_MAPPER, idCategoria).stream().findFirst();
    }

    @Override
    public List<CategoriaVO> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM categoria ORDER BY nombre",
                CATEGORIA_ROW_MAPPER);
    }

    @Override
    public Optional<CategoriaVO> findByNombre(String nombre) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM categoria WHERE LOWER(nombre) = LOWER(?)",
                CATEGORIA_ROW_MAPPER, nombre).stream().findFirst();
    }

    @Override
    public List<CategoriaVO> findByNombreContaining(String nombre) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM categoria WHERE LOWER(nombre) LIKE LOWER(?)",
                CATEGORIA_ROW_MAPPER, "%" + nombre + "%");
    }
}
