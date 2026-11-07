package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.CategoriaDAO;
import com.uniremingto.inventario.model.dao.DetalleVentaDAO;
import com.uniremingto.inventario.model.dao.MovimientoInventarioDAO;
import com.uniremingto.inventario.model.dao.ProductoDAO;
import com.uniremingto.inventario.model.dao.ProveedorDAO;
import com.uniremingto.inventario.model.dao.UsuarioDAO;
import com.uniremingto.inventario.model.dao.VentaDAO;
import com.uniremingto.inventario.model.vo.CategoriaVO;
import com.uniremingto.inventario.model.vo.DetalleVentaVO;
import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
import com.uniremingto.inventario.model.vo.ProductoVO;
import com.uniremingto.inventario.model.vo.ProveedorVO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import com.uniremingto.inventario.model.vo.VentaVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestControllersTest {

    @Mock
    private CategoriaDAO categoriaDAO;
    @Mock
    private ProveedorDAO proveedorDAO;
    @Mock
    private UsuarioDAO usuarioDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private VentaDAO ventaDAO;
    @Mock
    private DetalleVentaDAO detalleVentaDAO;
    @Mock
    private MovimientoInventarioDAO movimientoInventarioDAO;

    @InjectMocks
    private CategoriaRestController categoriaController;
    @InjectMocks
    private ProveedorRestController proveedorController;
    @InjectMocks
    private UsuarioRestController usuarioController;
    @InjectMocks
    private ProductoRestController productoController;
    @InjectMocks
    private VentaRestController ventaController;
    @InjectMocks
    private DetalleVentaRestController detalleVentaController;
    @InjectMocks
    private MovimientoInventarioRestController movimientoController;

    @Test
    void categoriaRestCubreCrudYNoEncontrados() {
        CategoriaVO categoria = new CategoriaVO();
        when(categoriaDAO.findAll()).thenReturn(List.of(categoria));
        when(categoriaDAO.findById(3)).thenReturn(Optional.of(categoria));
        when(categoriaDAO.findById(99)).thenReturn(Optional.empty());
        when(categoriaDAO.save(categoria)).thenReturn(categoria);
        when(categoriaDAO.update(any(CategoriaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoriaDAO.deleteById(3)).thenReturn(true);
        when(categoriaDAO.deleteById(99)).thenReturn(false);

        assertSame(categoria, categoriaController.listarCategorias().get(0));
        assertEquals(HttpStatus.OK, categoriaController.obtenerCategoria(3).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, categoriaController.obtenerCategoria(99).getStatusCode());
        assertEquals(HttpStatus.CREATED, categoriaController.crearCategoria(categoria).getStatusCode());
        assertEquals(HttpStatus.OK, categoriaController.actualizarCategoria(3, categoria).getStatusCode());
        assertEquals(3, categoria.getIdCategoria());
        assertEquals(HttpStatus.NO_CONTENT, categoriaController.eliminarCategoria(3).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, categoriaController.eliminarCategoria(99).getStatusCode());

        verify(categoriaDAO).save(categoria);
        verify(categoriaDAO).update(categoria);
    }

    @Test
    void proveedorRestCubreCrudYNoEncontrados() {
        ProveedorVO proveedor = new ProveedorVO();
        when(proveedorDAO.findAll()).thenReturn(List.of(proveedor));
        when(proveedorDAO.findById(4)).thenReturn(Optional.of(proveedor));
        when(proveedorDAO.findById(99)).thenReturn(Optional.empty());
        when(proveedorDAO.save(proveedor)).thenReturn(proveedor);
        when(proveedorDAO.update(any(ProveedorVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(proveedorDAO.deleteById(4)).thenReturn(true);
        when(proveedorDAO.deleteById(99)).thenReturn(false);

        assertSame(proveedor, proveedorController.listarProveedores().get(0));
        assertEquals(HttpStatus.OK, proveedorController.obtenerProveedor(4).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, proveedorController.obtenerProveedor(99).getStatusCode());
        assertEquals(HttpStatus.CREATED, proveedorController.crearProveedor(proveedor).getStatusCode());
        assertEquals(HttpStatus.OK, proveedorController.actualizarProveedor(4, proveedor).getStatusCode());
        assertEquals(4, proveedor.getIdProveedor());
        assertEquals(HttpStatus.NO_CONTENT, proveedorController.eliminarProveedor(4).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, proveedorController.eliminarProveedor(99).getStatusCode());

        verify(proveedorDAO).save(proveedor);
        verify(proveedorDAO).update(proveedor);
    }

    @Test
    void usuarioRestCubreCrudYNoEncontrados() {
        UsuarioVO usuario = new UsuarioVO();
        when(usuarioDAO.findAll()).thenReturn(List.of(usuario));
        when(usuarioDAO.findById(5)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.findById(99)).thenReturn(Optional.empty());
        when(usuarioDAO.save(usuario)).thenReturn(usuario);
        when(usuarioDAO.update(any(UsuarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioDAO.deleteById(5)).thenReturn(true);
        when(usuarioDAO.deleteById(99)).thenReturn(false);

        assertSame(usuario, usuarioController.listarUsuarios().get(0));
        assertEquals(HttpStatus.OK, usuarioController.obtenerUsuario(5).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, usuarioController.obtenerUsuario(99).getStatusCode());
        assertEquals(HttpStatus.CREATED, usuarioController.crearUsuario(usuario).getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.actualizarUsuario(5, usuario).getStatusCode());
        assertEquals(5, usuario.getIdUsuario());
        assertEquals(HttpStatus.NO_CONTENT, usuarioController.eliminarUsuario(5).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, usuarioController.eliminarUsuario(99).getStatusCode());

        verify(usuarioDAO).save(usuario);
        verify(usuarioDAO).update(usuario);
    }

    @Test
    void productoRestCubreCrudYNoEncontrados() {
        ProductoVO producto = new ProductoVO();
        when(productoDAO.findAll()).thenReturn(List.of(producto));
        when(productoDAO.findById(6)).thenReturn(Optional.of(producto));
        when(productoDAO.findById(99)).thenReturn(Optional.empty());
        when(productoDAO.save(producto)).thenReturn(producto);
        when(productoDAO.update(any(ProductoVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoDAO.deleteById(6)).thenReturn(true);
        when(productoDAO.deleteById(99)).thenReturn(false);

        assertSame(producto, productoController.listarProductos().get(0));
        assertEquals(HttpStatus.OK, productoController.obtenerProducto(6).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, productoController.obtenerProducto(99).getStatusCode());
        ResponseEntity<ProductoVO> createResponse = productoController.crearProducto(producto);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertSame(producto, createResponse.getBody());
        assertEquals(HttpStatus.OK, productoController.actualizarProducto(6, producto).getStatusCode());
        assertEquals(6, producto.getIdProducto());
        assertEquals(HttpStatus.NO_CONTENT, productoController.eliminarProducto(6).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, productoController.eliminarProducto(99).getStatusCode());

        verify(productoDAO).save(producto);
        verify(productoDAO).update(producto);
    }

    @Test
    void ventaRestCubreCrudYNoEncontrados() {
        VentaVO venta = new VentaVO();
        when(ventaDAO.findAll()).thenReturn(List.of(venta));
        when(ventaDAO.findById(7)).thenReturn(Optional.of(venta));
        when(ventaDAO.findById(99)).thenReturn(Optional.empty());
        when(ventaDAO.save(venta)).thenReturn(venta);
        when(ventaDAO.update(any(VentaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaDAO.deleteById(7)).thenReturn(true);
        when(ventaDAO.deleteById(99)).thenReturn(false);

        assertSame(venta, ventaController.listarVentas().get(0));
        assertEquals(HttpStatus.OK, ventaController.obtenerVenta(7).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, ventaController.obtenerVenta(99).getStatusCode());
        assertEquals(HttpStatus.CREATED, ventaController.crearVenta(venta).getStatusCode());
        assertEquals(HttpStatus.OK, ventaController.actualizarVenta(7, venta).getStatusCode());
        assertEquals(7, venta.getIdVenta());
        assertEquals(HttpStatus.NO_CONTENT, ventaController.eliminarVenta(7).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, ventaController.eliminarVenta(99).getStatusCode());

        verify(ventaDAO).save(venta);
        verify(ventaDAO).update(venta);
    }

    @Test
    void detalleVentaRestCubreCrudYNoEncontrados() {
        DetalleVentaVO detalle = new DetalleVentaVO();
        when(detalleVentaDAO.findAll()).thenReturn(List.of(detalle));
        when(detalleVentaDAO.findById(8)).thenReturn(Optional.of(detalle));
        when(detalleVentaDAO.findById(99)).thenReturn(Optional.empty());
        when(detalleVentaDAO.save(detalle)).thenReturn(detalle);
        when(detalleVentaDAO.update(any(DetalleVentaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(detalleVentaDAO.deleteById(8)).thenReturn(true);
        when(detalleVentaDAO.deleteById(99)).thenReturn(false);

        assertSame(detalle, detalleVentaController.listarDetalles().get(0));
        assertEquals(HttpStatus.OK, detalleVentaController.obtenerDetalle(8).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, detalleVentaController.obtenerDetalle(99).getStatusCode());
        assertEquals(HttpStatus.CREATED, detalleVentaController.crearDetalle(detalle).getStatusCode());
        assertEquals(HttpStatus.OK, detalleVentaController.actualizarDetalle(8, detalle).getStatusCode());
        assertEquals(8, detalle.getIdDetalle());
        assertEquals(HttpStatus.NO_CONTENT, detalleVentaController.eliminarDetalle(8).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, detalleVentaController.eliminarDetalle(99).getStatusCode());

        verify(detalleVentaDAO).save(detalle);
        verify(detalleVentaDAO).update(detalle);
    }

    @Test
    void movimientoRestCubreCrudYNoEncontrados() {
        MovimientoInventarioVO movimiento = new MovimientoInventarioVO();
        when(movimientoInventarioDAO.findAll()).thenReturn(List.of(movimiento));
        when(movimientoInventarioDAO.findById(9)).thenReturn(Optional.of(movimiento));
        when(movimientoInventarioDAO.findById(99)).thenReturn(Optional.empty());
        when(movimientoInventarioDAO.save(movimiento)).thenReturn(movimiento);
        when(movimientoInventarioDAO.update(any(MovimientoInventarioVO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(movimientoInventarioDAO.deleteById(9)).thenReturn(true);
        when(movimientoInventarioDAO.deleteById(99)).thenReturn(false);

        assertSame(movimiento, movimientoController.listarMovimientos().get(0));
        assertEquals(HttpStatus.OK, movimientoController.obtenerMovimiento(9).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, movimientoController.obtenerMovimiento(99).getStatusCode());
        assertEquals(HttpStatus.CREATED, movimientoController.crearMovimiento(movimiento).getStatusCode());
        assertEquals(HttpStatus.OK, movimientoController.actualizarMovimiento(9, movimiento).getStatusCode());
        assertEquals(9, movimiento.getIdMovimiento());
        assertEquals(HttpStatus.NO_CONTENT, movimientoController.eliminarMovimiento(9).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, movimientoController.eliminarMovimiento(99).getStatusCode());

        verify(movimientoInventarioDAO).save(movimiento);
        verify(movimientoInventarioDAO).update(movimiento);
    }
}
