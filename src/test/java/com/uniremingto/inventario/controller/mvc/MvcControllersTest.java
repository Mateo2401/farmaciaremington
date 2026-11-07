package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.controller.AuthController;
import com.uniremingto.inventario.controller.DashboardController;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MvcControllersTest {

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
    private CategoriaMvcController categoriaController;
    @InjectMocks
    private ProveedorMvcController proveedorController;
    @InjectMocks
    private UsuarioMvcController usuarioController;
    @InjectMocks
    private ProductoMvcController productoController;
    @InjectMocks
    private VentaMvcController ventaController;
    @InjectMocks
    private DetalleVentaMvcController detalleVentaController;
    @InjectMocks
    private MovimientoInventarioMvcController movimientoController;

    @Test
    void categoriaMvcListaCreaEditaYElimina() {
        CategoriaVO categoria = new CategoriaVO();
        when(categoriaDAO.findAll()).thenReturn(List.of(categoria));
        when(categoriaDAO.findById(4)).thenReturn(Optional.of(categoria));
        Model model = new ExtendedModelMap();

        assertEquals("categorias/lista", categoriaController.listarCategorias(model));
        assertSame(categoria, ((List<?>) model.getAttribute("categorias")).get(0));
        assertEquals("categorias/formulario", categoriaController.mostrarFormularioNuevo(model));
        assertInstanceOf(CategoriaVO.class, model.getAttribute("categoria"));
        assertEquals("redirect:/categorias", categoriaController.guardarCategoria(categoria));
        assertEquals("categorias/formulario", categoriaController.mostrarFormularioEdicion(4, model));
        assertSame(categoria, model.getAttribute("categoria"));
        assertEquals("redirect:/categorias", categoriaController.eliminarCategoria(4));

        verify(categoriaDAO).save(categoria);
        verify(categoriaDAO).deleteById(4);
    }

    @Test
    void proveedorMvcListaCreaEditaYElimina() {
        ProveedorVO proveedor = new ProveedorVO();
        when(proveedorDAO.findAll()).thenReturn(List.of(proveedor));
        when(proveedorDAO.findById(5)).thenReturn(Optional.of(proveedor));
        Model model = new ExtendedModelMap();

        assertEquals("proveedores/lista", proveedorController.listarProveedores(model));
        assertSame(proveedor, ((List<?>) model.getAttribute("proveedores")).get(0));
        assertEquals("proveedores/formulario", proveedorController.mostrarFormularioNuevo(model));
        assertInstanceOf(ProveedorVO.class, model.getAttribute("proveedor"));
        assertEquals("redirect:/proveedores", proveedorController.guardarProveedor(proveedor));
        assertEquals("proveedores/formulario", proveedorController.mostrarFormularioEdicion(5, model));
        assertSame(proveedor, model.getAttribute("proveedor"));
        assertEquals("redirect:/proveedores", proveedorController.eliminarProveedor(5));

        verify(proveedorDAO).save(proveedor);
        verify(proveedorDAO).deleteById(5);
    }

    @Test
    void usuarioMvcListaCreaEditaYElimina() {
        UsuarioVO usuario = new UsuarioVO();
        when(usuarioDAO.findAll()).thenReturn(List.of(usuario));
        when(usuarioDAO.findById(6)).thenReturn(Optional.of(usuario));
        Model model = new ExtendedModelMap();

        assertEquals("usuarios/lista", usuarioController.listarUsuarios(model));
        assertSame(usuario, ((List<?>) model.getAttribute("usuarios")).get(0));
        assertEquals("usuarios/formulario", usuarioController.mostrarFormularioNuevo(model));
        assertInstanceOf(UsuarioVO.class, model.getAttribute("usuario"));
        assertEquals("redirect:/usuarios", usuarioController.guardarUsuario(usuario));
        assertEquals("usuarios/formulario", usuarioController.mostrarFormularioEdicion(6, model));
        assertSame(usuario, model.getAttribute("usuario"));
        assertEquals("redirect:/usuarios", usuarioController.eliminarUsuario(6));

        verify(usuarioDAO).save(usuario);
        verify(usuarioDAO).deleteById(6);
    }

    @Test
    void ventaMvcListaCreaEditaYElimina() {
        VentaVO venta = new VentaVO();
        when(ventaDAO.findAll()).thenReturn(List.of(venta));
        when(ventaDAO.findById(7)).thenReturn(Optional.of(venta));
        Model model = new ExtendedModelMap();

        assertEquals("ventas/lista", ventaController.listarVentas(model));
        assertSame(venta, ((List<?>) model.getAttribute("ventas")).get(0));
        assertEquals("ventas/formulario", ventaController.mostrarFormularioNuevo(model));
        assertInstanceOf(VentaVO.class, model.getAttribute("venta"));
        assertEquals("redirect:/ventas", ventaController.guardarVenta(venta));
        assertEquals("ventas/formulario", ventaController.mostrarFormularioEdicion(7, model));
        assertSame(venta, model.getAttribute("venta"));
        assertEquals("redirect:/ventas", ventaController.eliminarVenta(7));

        verify(ventaDAO).save(venta);
        verify(ventaDAO).deleteById(7);
    }

    @Test
    void detalleVentaMvcListaCreaEditaYElimina() {
        DetalleVentaVO detalle = new DetalleVentaVO();
        when(detalleVentaDAO.findAll()).thenReturn(List.of(detalle));
        when(detalleVentaDAO.findById(8)).thenReturn(Optional.of(detalle));
        Model model = new ExtendedModelMap();

        assertEquals("detalle-ventas/lista", detalleVentaController.listarDetalles(model));
        assertSame(detalle, ((List<?>) model.getAttribute("detallesVenta")).get(0));
        assertEquals("detalle-ventas/formulario", detalleVentaController.mostrarFormularioNuevo(model));
        assertInstanceOf(DetalleVentaVO.class, model.getAttribute("detalleVenta"));
        assertEquals("redirect:/detalle-ventas", detalleVentaController.guardarDetalle(detalle));
        assertEquals("detalle-ventas/formulario", detalleVentaController.mostrarFormularioEdicion(8, model));
        assertSame(detalle, model.getAttribute("detalleVenta"));
        assertEquals("redirect:/detalle-ventas", detalleVentaController.eliminarDetalle(8));

        verify(detalleVentaDAO).save(detalle);
        verify(detalleVentaDAO).deleteById(8);
    }

    @Test
    void movimientoMvcListaCreaEditaYElimina() {
        MovimientoInventarioVO movimiento = new MovimientoInventarioVO();
        when(movimientoInventarioDAO.findAll()).thenReturn(List.of(movimiento));
        when(movimientoInventarioDAO.findById(9)).thenReturn(Optional.of(movimiento));
        Model model = new ExtendedModelMap();

        assertEquals("movimientos-inventario/lista", movimientoController.listarMovimientos(model));
        assertSame(movimiento, ((List<?>) model.getAttribute("movimientosInventario")).get(0));
        assertEquals("movimientos-inventario/formulario", movimientoController.mostrarFormularioNuevo(model));
        assertInstanceOf(MovimientoInventarioVO.class, model.getAttribute("movimientoInventario"));
        assertEquals("redirect:/movimientos-inventario", movimientoController.guardarMovimiento(movimiento));
        assertEquals("movimientos-inventario/formulario", movimientoController.mostrarFormularioEdicion(9, model));
        assertSame(movimiento, model.getAttribute("movimientoInventario"));
        assertEquals("redirect:/movimientos-inventario", movimientoController.eliminarMovimiento(9));

        verify(movimientoInventarioDAO).save(movimiento);
        verify(movimientoInventarioDAO).deleteById(9);
    }

    @Test
    void productoMvcUsaSaveAlCrearYUpdateAlEditar() {
        ProductoVO producto = productoValido();
        when(productoDAO.findAll()).thenReturn(List.of(producto));
        when(productoDAO.findById(10)).thenReturn(Optional.of(producto));
        Model model = new ExtendedModelMap();

        assertEquals("productos/lista", productoController.listarProductos(model));
        assertSame(producto, ((List<?>) model.getAttribute("productos")).get(0));
        assertEquals("productos/formulario", productoController.mostrarFormularioNuevo(model));
        ProductoVO nuevo = (ProductoVO) model.getAttribute("producto");
        assertEquals(0, nuevo.getStock());
        assertEquals(5, nuevo.getStockMinimo());
        assertEquals(true, nuevo.getEstado());

        BindingResult createResult = bindingResult(producto);
        RedirectAttributesModelMap createRedirect = new RedirectAttributesModelMap();
        assertEquals("redirect:/productos", productoController.guardarProducto(producto, createResult, model, createRedirect));
        assertEquals("El producto fue guardado.", createRedirect.getFlashAttributes().get("mensaje"));

        ProductoVO editado = productoValido();
        BindingResult updateResult = bindingResult(editado);
        RedirectAttributesModelMap updateRedirect = new RedirectAttributesModelMap();
        assertEquals("redirect:/productos", productoController.actualizarProducto(10, editado, updateResult, model, updateRedirect));
        assertEquals(10, editado.getIdProducto());
        assertEquals("El producto fue actualizado.", updateRedirect.getFlashAttributes().get("mensaje"));

        assertEquals("productos/formulario", productoController.mostrarFormularioEdicion(10, model));
        assertSame(producto, model.getAttribute("producto"));
        verify(productoDAO).save(producto);
        verify(productoDAO).update(editado);
    }

    @Test
    void productoMvcRechazaDatosInvalidosYSenalaCodigoDuplicado() {
        Model model = new ExtendedModelMap();
        ProductoVO invalido = new ProductoVO();
        BindingResult invalidResult = bindingResult(invalido);
        assertEquals("productos/formulario", productoController.guardarProducto(invalido, invalidResult, model,
                new RedirectAttributesModelMap()));
        assertTrue(invalidResult.hasErrors());
        verifyNoInteractions(productoDAO);

        ProductoVO duplicado = productoValido();
        when(productoDAO.save(duplicado)).thenThrow(new DuplicateKeyException("duplicate"));
        assertEquals("productos/formulario", productoController.guardarProducto(duplicado,
                bindingResult(duplicado), model, new RedirectAttributesModelMap()));
        assertEquals("Ya existe otro producto con ese código.", model.getAttribute("formError"));
        verify(productoDAO).save(duplicado);
    }

    @Test
    void productoMvcEliminaYManejaReferenciasDeBaseDatos() {
        when(productoDAO.deleteById(10)).thenReturn(true);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        assertEquals("redirect:/productos", productoController.eliminarProducto(10, redirect));
        assertEquals("El producto fue eliminado.", redirect.getFlashAttributes().get("mensaje"));
        verify(productoDAO).deleteById(10);

        when(productoDAO.deleteById(11)).thenReturn(false);
        RedirectAttributesModelMap notFoundRedirect = new RedirectAttributesModelMap();
        assertEquals("redirect:/productos", productoController.eliminarProducto(11, notFoundRedirect));
        assertEquals("El producto ya no existe.", notFoundRedirect.getFlashAttributes().get("error"));
        verify(productoDAO).deleteById(11);
    }

    @Test
    void authMvcMuestraLogin() {
        assertEquals("login", new AuthController().login());
    }

    @Test
    void dashboardMvcAgregaUsuarioYRolesAlModelo() {
        DashboardController controller = new DashboardController();
        Model model = new ExtendedModelMap();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin@farmacia.com", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            assertEquals("dashboard", controller.dashboard(model));
            assertEquals("admin@farmacia.com", model.getAttribute("username"));
            assertEquals(authentication.getAuthorities(), model.getAttribute("roles"));
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private static ProductoVO productoValido() {
        ProductoVO producto = new ProductoVO();
        producto.setCodigo("TEST001");
        producto.setNombre("Producto de prueba");
        producto.setIdCategoria(1);
        producto.setPrecioCompra(BigDecimal.ZERO);
        producto.setPrecioVenta(BigDecimal.ZERO);
        producto.setStock(0);
        producto.setStockMinimo(5);
        producto.setEstado(true);
        return producto;
    }

    private static BindingResult bindingResult(ProductoVO producto) {
        return new BeanPropertyBindingResult(producto, "producto");
    }
}
