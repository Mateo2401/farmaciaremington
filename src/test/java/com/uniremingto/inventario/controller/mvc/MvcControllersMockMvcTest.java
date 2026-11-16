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
import com.uniremingto.inventario.model.vo.MovimientoInventarioVO;
import com.uniremingto.inventario.model.vo.ProductoVO;
import com.uniremingto.inventario.model.vo.ProveedorVO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import com.uniremingto.inventario.model.vo.VentaVO;
import com.uniremingto.inventario.service.UsuarioService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
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
        @Mock private PasswordEncoder passwordEncoder;
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
                new UsuarioMvcController(new UsuarioService(usuarioDAO, passwordEncoder)),
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
        mockMvc.perform(post("/categorias")
                        .param("nombre", "Vitaminas")
                        .param("descripcion", "Suplementos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"))
                .andExpect(flash().attribute("mensaje", "La categoría fue guardada."));
        mockMvc.perform(post("/categorias/3/eliminar"))
                .andExpect(status().is3xxRedirection());

                ArgumentCaptor<CategoriaVO> categoriaCaptor = ArgumentCaptor.forClass(CategoriaVO.class);
                verify(categoriaDAO).save(categoriaCaptor.capture());
                assertEquals("Vitaminas", categoriaCaptor.getValue().getNombre());
                assertEquals("Suplementos", categoriaCaptor.getValue().getDescripcion());
        verify(categoriaDAO).deleteById(3);
    }

        @Test
        void categoriaDuplicadaVuelveAlFormularioConError() throws Exception {
                when(categoriaDAO.save(any(CategoriaVO.class))).thenThrow(new DuplicateKeyException("duplicate"));

                mockMvc.perform(post("/categorias").param("nombre", "Vitaminas"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("categorias/formulario"))
                                .andExpect(model().attribute("formError", "Ya existe una categoría con ese nombre."));
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
        void proveedorNuevoSeEnlazaAlFormularioYSeEnviaAlDao() throws Exception {
                mockMvc.perform(post("/proveedores")
                                                .param("nombre", "Distribuidora de prueba")
                                                .param("telefono", "3001234567")
                                                .param("correo", "proveedor@test.com")
                                                .param("direccion", "Calle 10")
                                                .param("estado", "true"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/proveedores"))
                                .andExpect(flash().attribute("mensaje", "El proveedor fue guardado."));

                ArgumentCaptor<ProveedorVO> captor = ArgumentCaptor.forClass(ProveedorVO.class);
                verify(proveedorDAO).save(captor.capture());
                assertEquals("Distribuidora de prueba", captor.getValue().getNombre());
                assertEquals("proveedor@test.com", captor.getValue().getCorreo());
                assertEquals(true, captor.getValue().getEstado());
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
        when(passwordEncoder.encode("ClaveSegura123")).thenReturn("$2a$hash-usuario");
        mockMvc.perform(post("/usuarios")
                        .param("nombre", "Usuario test")
                        .param("correo", "usuario@test.com")
                        .param("contrasena", "ClaveSegura123")
                        .param("rol", "VENDEDOR")
                        .param("estado", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attribute("mensaje", "El usuario fue registrado."));
        mockMvc.perform(post("/usuarios/5/eliminar"))
                .andExpect(redirectedUrl("/usuarios"));

        ArgumentCaptor<UsuarioVO> usuarioCaptor = ArgumentCaptor.forClass(UsuarioVO.class);
        verify(usuarioDAO).save(usuarioCaptor.capture());
        assertEquals("usuario@test.com", usuarioCaptor.getValue().getCorreo());
        assertEquals("$2a$hash-usuario", usuarioCaptor.getValue().getContrasena());
        verify(passwordEncoder).encode("ClaveSegura123");
        verify(usuarioDAO).deleteById(5);
    }

        @Test
        void usuarioInvalidoRegresaAlFormularioSinPersistirse() throws Exception {
                mockMvc.perform(post("/usuarios").param("nombre", ""))
                                .andExpect(status().isOk())
                                .andExpect(view().name("usuarios/formulario"));

                verifyNoInteractions(usuarioDAO, passwordEncoder);
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
        mockMvc.perform(post("/ventas")
                        .param("idUsuario", "1")
                        .param("total", "14.30")
                        .param("metodoPago", "EFECTIVO")
                        .param("estado", "COMPLETADA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ventas"))
                .andExpect(flash().attribute("mensaje", "La venta fue registrada."));
        mockMvc.perform(post("/ventas/6/eliminar"))
                .andExpect(redirectedUrl("/ventas"));

        ArgumentCaptor<VentaVO> ventaCaptor = ArgumentCaptor.forClass(VentaVO.class);
        verify(ventaDAO).save(ventaCaptor.capture());
        assertEquals(1, ventaCaptor.getValue().getIdUsuario());
        assertEquals(new BigDecimal("14.30"), ventaCaptor.getValue().getTotal());
        verify(ventaDAO).deleteById(6);
    }

        @Test
        void ventaRechazaCamposRequeridosInvalidosSinLlamarAlDao() throws Exception {
                mockMvc.perform(post("/ventas")
                                                .param("idUsuario", "0")
                                                .param("total", "-1")
                                                .param("metodoPago", ""))
                                .andExpect(status().isOk())
                                .andExpect(view().name("ventas/formulario"));

                verifyNoInteractions(ventaDAO);
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
        mockMvc.perform(post("/movimientos-inventario")
                        .param("idProducto", "2")
                        .param("idUsuario", "1")
                        .param("tipo", "ENTRADA")
                        .param("cantidad", "30")
                        .param("descripcion", "Reposición de prueba"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movimientos-inventario"))
                .andExpect(flash().attribute("mensaje", "El movimiento fue registrado."));
        mockMvc.perform(post("/movimientos-inventario/8/eliminar"))
                .andExpect(redirectedUrl("/movimientos-inventario"));

        ArgumentCaptor<MovimientoInventarioVO> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventarioVO.class);
        verify(movimientoDAO).save(movimientoCaptor.capture());
        assertEquals(2, movimientoCaptor.getValue().getIdProducto());
        assertEquals(1, movimientoCaptor.getValue().getIdUsuario());
        assertEquals(30, movimientoCaptor.getValue().getCantidad());
        verify(movimientoDAO).deleteById(8);
    }

    @Test
    void movimientoRechazaCantidadOCamposInvalidosSinLlamarAlDao() throws Exception {
        mockMvc.perform(post("/movimientos-inventario")
                        .param("idProducto", "0")
                        .param("idUsuario", "-1")
                        .param("tipo", "TIPO_INVALIDO")
                        .param("cantidad", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("movimientos-inventario/formulario"));

        verifyNoInteractions(movimientoDAO);
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
