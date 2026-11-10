
package com.uniremingto.inventario.model.dao;

import com.uniremingto.inventario.model.vo.ProductoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoDAOImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductoDAOImpl productoDAO;

    private ProductoVO producto;

    @BeforeEach
    void setUp() {
        producto = new ProductoVO();
        producto.setIdProducto(1);
        producto.setCodigo("MED001");
        producto.setNombre("Acetaminofen");
        producto.setDescripcion("Analgesico");
        producto.setIdCategoria(1);
        producto.setIdProveedor(1);
        producto.setPrecioCompra(new BigDecimal("5000"));
        producto.setPrecioVenta(new BigDecimal("7000"));
        producto.setStock(20);
        producto.setStockMinimo(5);
        producto.setEstado(true);
    }

    @Test
    void buscarPorIdProductoExistente() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        Optional<ProductoVO> resultado = productoDAO.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Acetaminofen", resultado.get().getNombre());
    }

    @Test
    void buscarPorIdProductoInexistente() {
        doReturn(List.of()).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        Optional<ProductoVO> resultado = productoDAO.findById(99);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarTodosLosProductos() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any());

        List<ProductoVO> resultado = productoDAO.findAll();

        assertEquals(1, resultado.size());
        assertEquals("MED001", resultado.get(0).getCodigo());
    }

    @Test
    void buscarPorCodigoExistente() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        Optional<ProductoVO> resultado = productoDAO.findByCodigo("MED001");

        assertTrue(resultado.isPresent());
        assertEquals("MED001", resultado.get().getCodigo());
    }

    @Test
    void buscarPorCodigoInexistente() {
        doReturn(List.of()).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        Optional<ProductoVO> resultado = productoDAO.findByCodigo("NOEXISTE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorNombre() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        List<ProductoVO> resultado =
                productoDAO.findByNombreContaining("Acetaminofen");

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorCategoria() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        List<ProductoVO> resultado = productoDAO.findByCategoria(1);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorProveedor() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        List<ProductoVO> resultado = productoDAO.findByProveedor(1);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorEstado() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        List<ProductoVO> resultado = productoDAO.findByEstado(true);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarProductosConStockMenor() {
        doReturn(List.of(producto)).when(jdbcTemplate)
                .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

        List<ProductoVO> resultado = productoDAO.findByStockLessThan(10);

        assertEquals(1, resultado.size());
    }

   @Test
void buscarProductosConStockBajo() {
    doReturn(List.of(producto)).when(jdbcTemplate)
            .query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<ProductoVO>>any(), any(Object[].class));

    List<ProductoVO> resultado = productoDAO.findLowStockProducts(5);

    assertEquals(1, resultado.size());
}

    @Test
    void eliminarProductoExistente() {
        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(1);

        boolean resultado = productoDAO.deleteById(1);

        assertTrue(resultado);
    }

    @Test
    void eliminarProductoInexistente() {
        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(0);

        boolean resultado = productoDAO.deleteById(99);

        assertFalse(resultado);
    }

    @Test
    void actualizarProductoSinIdDebeLanzarExcepcion() {
        producto.setIdProducto(null);

        assertThrows(IllegalArgumentException.class,
                () -> productoDAO.update(producto));

        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void actualizarProductoInexistenteDebeLanzarExcepcion() {
       when(jdbcTemplate.update(
        any(PreparedStatementCreator.class)
)).thenReturn(0);

        assertThrows(IllegalArgumentException.class,
                () -> productoDAO.update(producto));
    }

    @Test
    void actualizarProductoExistente() {
        when(jdbcTemplate.update(
        any(PreparedStatementCreator.class)
)).thenReturn(1);

        assertDoesNotThrow(() -> productoDAO.update(producto));
    }

    @Test
    void guardarProductoDebeAsignarIdGenerado() {
        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(
                    Map.of("id_producto", 100L)
            );
            return 1;
        }).when(jdbcTemplate).update(
                any(PreparedStatementCreator.class),
                any(KeyHolder.class)
        );

        producto.setIdProducto(null);

        productoDAO.save(producto);

        assertEquals(100, producto.getIdProducto());
    }
}
