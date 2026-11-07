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
import com.uniremingto.inventario.model.vo.ProductoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class MvcControllersMockMvcTest {

    @Mock private CategoriaDAO categoriaDAO;
    @Mock private ProveedorDAO proveedorDAO;
    @Mock private UsuarioDAO usuarioDAO;
    @Mock private ProductoDAO productoDAO;
    @Mock private VentaDAO ventaDAO;
    @Mock private DetalleVentaDAO detalleVentaDAO;
    @Mock private MovimientoInventarioDAO movimientoDAO;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CategoriaMvcController(categoriaDAO),
                new ProveedorMvcController(proveedorDAO),
                new UsuarioMvcController(usuarioDAO),
                new ProductoMvcController(productoDAO),
                new VentaMvcController(ventaDAO),
                new DetalleVentaMvcController(detalleVentaDAO),
                new MovimientoInventarioMvcController(movimientoDAO),
                new AuthController(),
                                new DashboardController())
                                .setViewResolvers((viewName, locale) -> {
                                        if (viewName.startsWith("redirect:")) {
                                                return new RedirectView(viewName.substring("redirect:".length()));
                                        }
                                        return new AbstractView() {
                                                @Override
                                                protected void renderMergedOutputModel(Map<String, Object> model,
                                                                                                                           HttpServletRequest request,
                                                                                                                           HttpServletResponse response) {
                                                }
                                        };
                                })
                                .build();
    }

    @Test
    void categoriasRenderizanVistasYDeleganCrud() throws Exception {
        when(categoriaDAO.findAll()).thenReturn(List.of());
        when(categoriaDAO.findById(3)).thenReturn(Optional.of(new com.uniremingto.inventario.model.vo.CategoriaVO()));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/lista"))
                .andExpect(model().attributeExists("categorias"));
        mockMvc.perform(get("/categorias/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorias/formulario"))
                .andExpect(model().attributeExists("categoria"));
        mockMvc.perform(get("/categorias/3/editar"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categoria"));
        mockMvc.perform(post("/categorias").param("nombre", "Vitaminas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));
        mockMvc.perform(post("/categorias/3/eliminar"))
                .andExpect(status().is3xxRedirection());

        verify(categoriaDAO).save(any());
        verify(categoriaDAO).deleteById(3);
    }

    @Test
    void proveedoresRenderizanVistasYDeleganCrud() throws Exception {
        when(proveedorDAO.findAll()).thenReturn(List.of());
        when(proveedorDAO.findById(4)).thenReturn(Optional.of(new com.uniremingto.inventario.model.vo.ProveedorVO()));

        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isOk())
                .andExpect(view().name("proveedores/lista"))
                .andExpect(model().attributeExists("proveedores"));
        mockMvc.perform(get("/proveedores/nuevo"))
                .andExpect(view().name("proveedores/formulario"))
                .andExpect(model().attributeExists("proveedor"));
        mockMvc.perform(get("/proveedores/4/editar"))
                .andExpect(model().attributeExists("proveedor"));
        mockMvc.perform(post("/proveedores").param("nombre", "Proveedor test"))
                .andExpect(redirectedUrl("/proveedores"));
        mockMvc.perform(post("/proveedores/4/eliminar"))
                .andExpect(redirectedUrl("/proveedores"));

        verify(proveedorDAO).save(any());
        verify(proveedorDAO).deleteById(4);
    }

    @Test
    void usuariosRenderizanVistasYDeleganCrud() throws Exception {
        when(usuarioDAO.findAll()).thenReturn(List.of());
        when(usuarioDAO.findById(5)).thenReturn(Optional.of(new com.uniremingto.inventario.model.vo.UsuarioVO()));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"))
                .andExpect(model().attributeExists("usuarios"));
        mockMvc.perform(get("/usuarios/nuevo"))
                .andExpect(view().name("usuarios/formulario"))
                .andExpect(model().attributeExists("usuario"));
        mockMvc.perform(get("/usuarios/5/editar"))
                .andExpect(model().attributeExists("usuario"));
        mockMvc.perform(post("/usuarios").param("nombre", "Usuario test"))
                .andExpect(redirectedUrl("/usuarios"));
        mockMvc.perform(post("/usuarios/5/eliminar"))
                .andExpect(redirectedUrl("/usuarios"));

        verify(usuarioDAO).save(any());
        verify(usuarioDAO).deleteById(5);
    }

    @Test
    void ventasRenderizanVistasYDeleganCrud() throws Exception {
        when(ventaDAO.findAll()).thenReturn(List.of());
        when(ventaDAO.findById(6)).thenReturn(Optional.of(new com.uniremingto.inventario.model.vo.VentaVO()));

        mockMvc.perform(get("/ventas"))
                .andExpect(status().isOk())
                .andExpect(view().name("ventas/lista"))
                .andExpect(model().attributeExists("ventas"));
        mockMvc.perform(get("/ventas/nuevo"))
                .andExpect(view().name("ventas/formulario"))
                .andExpect(model().attributeExists("venta"));
        mockMvc.perform(get("/ventas/6/editar"))
                .andExpect(model().attributeExists("venta"));
        mockMvc.perform(post("/ventas").param("metodoPago", "EFECTIVO"))
                .andExpect(redirectedUrl("/ventas"));
        mockMvc.perform(post("/ventas/6/eliminar"))
                .andExpect(redirectedUrl("/ventas"));

        verify(ventaDAO).save(any());
        verify(ventaDAO).deleteById(6);
    }

    @Test
    void detallesVentaRenderizanVistasYDeleganCrud() throws Exception {
        when(detalleVentaDAO.findAll()).thenReturn(List.of());
        when(detalleVentaDAO.findById(7)).thenReturn(Optional.of(new com.uniremingto.inventario.model.vo.DetalleVentaVO()));

        mockMvc.perform(get("/detalle-ventas"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-ventas/lista"))
                .andExpect(model().attributeExists("detallesVenta"));
        mockMvc.perform(get("/detalle-ventas/nuevo"))
                .andExpect(view().name("detalle-ventas/formulario"))
                .andExpect(model().attributeExists("detalleVenta"));
        mockMvc.perform(get("/detalle-ventas/7/editar"))
                .andExpect(model().attributeExists("detalleVenta"));
        mockMvc.perform(post("/detalle-ventas").param("cantidad", "2"))
                .andExpect(redirectedUrl("/detalle-ventas"));
        mockMvc.perform(post("/detalle-ventas/7/eliminar"))
                .andExpect(redirectedUrl("/detalle-ventas"));

        verify(detalleVentaDAO).save(any());
        verify(detalleVentaDAO).deleteById(7);
    }

    @Test
    void movimientosRenderizanVistasYDeleganCrud() throws Exception {
        when(movimientoDAO.findAll()).thenReturn(List.of());
        when(movimientoDAO.findById(8)).thenReturn(Optional.of(new com.uniremingto.inventario.model.vo.MovimientoInventarioVO()));

        mockMvc.perform(get("/movimientos-inventario"))
                .andExpect(status().isOk())
                .andExpect(view().name("movimientos-inventario/lista"))
                .andExpect(model().attributeExists("movimientosInventario"));
        mockMvc.perform(get("/movimientos-inventario/nuevo"))
                .andExpect(view().name("movimientos-inventario/formulario"))
                .andExpect(model().attributeExists("movimientoInventario"));
        mockMvc.perform(get("/movimientos-inventario/8/editar"))
                .andExpect(model().attributeExists("movimientoInventario"));
        mockMvc.perform(post("/movimientos-inventario").param("tipo", "ENTRADA"))
                .andExpect(redirectedUrl("/movimientos-inventario"));
        mockMvc.perform(post("/movimientos-inventario/8/eliminar"))
                .andExpect(redirectedUrl("/movimientos-inventario"));

        verify(movimientoDAO).save(any());
        verify(movimientoDAO).deleteById(8);
    }

    @Test
    void productoNuevoSeGuardaYRedirigeALaLista() throws Exception {
        mockMvc.perform(get("/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeExists("producto"));

        mockMvc.perform(post("/productos")
                        .param("codigo", "MED900")
                        .param("nombre", "Producto test")
                        .param("idCategoria", "1")
                        .param("precioCompra", "1000.00")
                        .param("precioVenta", "1500.00")
                        .param("stock", "12")
                        .param("stockMinimo", "3")
                        .param("estado", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attribute("mensaje", "El producto fue guardado."));

        ArgumentCaptor<ProductoVO> captor = ArgumentCaptor.forClass(ProductoVO.class);
        verify(productoDAO).save(captor.capture());
        assertEquals("MED900", captor.getValue().getCodigo());
        assertEquals(1, captor.getValue().getIdCategoria());
        assertEquals(new BigDecimal("1500.00"), captor.getValue().getPrecioVenta());
    }

    @Test
    void productoEditadoActualizaConElIdDeLaRuta() throws Exception {
        mockMvc.perform(post("/productos/27")
                        .param("codigo", "MED027")
                        .param("nombre", "Producto actualizado")
                        .param("idCategoria", "2")
                        .param("precioCompra", "800.00")
                        .param("precioVenta", "1200.00")
                        .param("stock", "20")
                        .param("stockMinimo", "5")
                        .param("estado", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attribute("mensaje", "El producto fue actualizado."));

        ArgumentCaptor<ProductoVO> captor = ArgumentCaptor.forClass(ProductoVO.class);
        verify(productoDAO).update(captor.capture());
        assertEquals(27, captor.getValue().getIdProducto());
        verify(productoDAO, never()).save(any());
    }

    @Test
    void productoRechazaDatosInvalidosYSenalaDuplicados() throws Exception {
        mockMvc.perform(post("/productos").param("codigo", "MED901"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"));
        verifyNoInteractions(productoDAO);

        when(productoDAO.save(any())).thenThrow(new DuplicateKeyException("duplicate"));
        mockMvc.perform(post("/productos")
                        .param("codigo", "MED001")
                        .param("nombre", "Producto duplicado")
                        .param("idCategoria", "1")
                        .param("precioCompra", "10")
                        .param("precioVenta", "20")
                        .param("stock", "1")
                        .param("stockMinimo", "1")
                        .param("estado", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attribute("formError", "Ya existe otro producto con ese código."));
    }

    @Test
    void productoEliminadoManejaExitoYRestriccionesDeClavesForaneas() throws Exception {
        when(productoDAO.deleteById(12)).thenReturn(true);
        mockMvc.perform(post("/productos/12/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos"))
                .andExpect(flash().attribute("mensaje", "El producto fue eliminado."));

        when(productoDAO.deleteById(13)).thenThrow(new DataIntegrityViolationException("foreign key"));
        mockMvc.perform(post("/productos/13/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error",
                        "No se puede eliminar el producto porque está asociado a ventas o movimientos de inventario."));
    }

    @Test
    void loginYDashboardRenderizanSusVistas() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "admin@farmacia.com", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        try {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("dashboard"))
                    .andExpect(model().attribute("username", "admin@farmacia.com"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
