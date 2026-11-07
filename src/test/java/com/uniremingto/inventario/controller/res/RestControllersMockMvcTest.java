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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestControllersMockMvcTest {

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
                new CategoriaRestController(categoriaDAO),
                new ProveedorRestController(proveedorDAO),
                new UsuarioRestController(usuarioDAO),
                new ProductoRestController(productoDAO),
                new VentaRestController(ventaDAO),
                new DetalleVentaRestController(detalleVentaDAO),
                new MovimientoInventarioRestController(movimientoDAO)).build();
    }

    @Test
    void categoriaRestExponeCrudYRespuestasNotFound() throws Exception {
        CategoriaVO categoria = new CategoriaVO(2, "Analgésicos", "Alivio del dolor");
        when(categoriaDAO.findAll()).thenReturn(List.of(categoria));
        when(categoriaDAO.findById(2)).thenReturn(Optional.of(categoria));
        when(categoriaDAO.findById(99)).thenReturn(Optional.empty());
        when(categoriaDAO.save(any(CategoriaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoriaDAO.update(any(CategoriaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoriaDAO.deleteById(2)).thenReturn(true);
        when(categoriaDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Analgésicos"));
        mockMvc.perform(get("/api/categorias/2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idCategoria").value(2));
        mockMvc.perform(get("/api/categorias/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Vitaminas\",\"descripcion\":\"Suplementos\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.nombre").value("Vitaminas"));
        mockMvc.perform(put("/api/categorias/3").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Vitaminas\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idCategoria").value(3));
        mockMvc.perform(delete("/api/categorias/2")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/categorias/99")).andExpect(status().isNotFound());

        ArgumentCaptor<CategoriaVO> captor = ArgumentCaptor.forClass(CategoriaVO.class);
        verify(categoriaDAO).save(captor.capture());
        assertEquals("Vitaminas", captor.getValue().getNombre());
        verify(categoriaDAO).update(any(CategoriaVO.class));
    }

    @Test
    void proveedorRestExponeCrudYRespuestasNotFound() throws Exception {
        ProveedorVO proveedor = new ProveedorVO(4, "Distribuidora", "3000000000", "contacto@test.com", "Calle 1", true);
        when(proveedorDAO.findAll()).thenReturn(List.of(proveedor));
        when(proveedorDAO.findById(4)).thenReturn(Optional.of(proveedor));
        when(proveedorDAO.findById(99)).thenReturn(Optional.empty());
        when(proveedorDAO.save(any(ProveedorVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(proveedorDAO.update(any(ProveedorVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(proveedorDAO.deleteById(4)).thenReturn(true);
        when(proveedorDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Distribuidora"));
        mockMvc.perform(get("/api/proveedores/4")).andExpect(status().isOk());
        mockMvc.perform(get("/api/proveedores/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/proveedores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Proveedor nuevo\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.nombre").value("Proveedor nuevo"));
        mockMvc.perform(put("/api/proveedores/5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Proveedor editado\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idProveedor").value(5));
        mockMvc.perform(delete("/api/proveedores/4")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/proveedores/99")).andExpect(status().isNotFound());

        verify(proveedorDAO).save(any(ProveedorVO.class));
        verify(proveedorDAO).update(any(ProveedorVO.class));
    }

    @Test
    void usuarioRestExponeCrudYRespuestasNotFound() throws Exception {
        UsuarioVO usuario = new UsuarioVO();
        usuario.setIdUsuario(6);
        usuario.setNombre("Vendedor");
        usuario.setCorreo("vendedor@test.com");
        usuario.setRol("VENDEDOR");
        when(usuarioDAO.findAll()).thenReturn(List.of(usuario));
        when(usuarioDAO.findById(6)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.findById(99)).thenReturn(Optional.empty());
        when(usuarioDAO.save(any(UsuarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioDAO.update(any(UsuarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioDAO.deleteById(6)).thenReturn(true);
        when(usuarioDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].correo").value("vendedor@test.com"));
        mockMvc.perform(get("/api/usuarios/6")).andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Nuevo\",\"correo\":\"nuevo@test.com\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/usuarios/7").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Editado\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idUsuario").value(7));
        mockMvc.perform(delete("/api/usuarios/6")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/usuarios/99")).andExpect(status().isNotFound());

        verify(usuarioDAO).save(any(UsuarioVO.class));
        verify(usuarioDAO).update(any(UsuarioVO.class));
    }

    @Test
    void productoRestExponeCrudYRespuestasNotFound() throws Exception {
        ProductoVO producto = new ProductoVO();
        producto.setIdProducto(8);
        producto.setCodigo("MED008");
        producto.setNombre("Producto REST");
        when(productoDAO.findAll()).thenReturn(List.of(producto));
        when(productoDAO.findById(8)).thenReturn(Optional.of(producto));
        when(productoDAO.findById(99)).thenReturn(Optional.empty());
        when(productoDAO.save(any(ProductoVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoDAO.update(any(ProductoVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoDAO.deleteById(8)).thenReturn(true);
        when(productoDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value("MED008"));
        mockMvc.perform(get("/api/productos/8")).andExpect(status().isOk());
        mockMvc.perform(get("/api/productos/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"MED009\",\"nombre\":\"Nuevo\",\"idCategoria\":1," +
                                "\"precioCompra\":1000,\"precioVenta\":1500,\"stock\":1,\"stockMinimo\":1}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.codigo").value("MED009"));
        mockMvc.perform(put("/api/productos/10").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"MED010\",\"nombre\":\"Editado\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idProducto").value(10));
        mockMvc.perform(delete("/api/productos/8")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/productos/99")).andExpect(status().isNotFound());

        verify(productoDAO).save(any(ProductoVO.class));
        verify(productoDAO).update(any(ProductoVO.class));
    }

    @Test
    void ventaRestExponeCrudYRespuestasNotFound() throws Exception {
        VentaVO venta = new VentaVO();
        venta.setIdVenta(11);
        venta.setMetodoPago("EFECTIVO");
        when(ventaDAO.findAll()).thenReturn(List.of(venta));
        when(ventaDAO.findById(11)).thenReturn(Optional.of(venta));
        when(ventaDAO.findById(99)).thenReturn(Optional.empty());
        when(ventaDAO.save(any(VentaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaDAO.update(any(VentaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ventaDAO.deleteById(11)).thenReturn(true);
        when(ventaDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].metodoPago").value("EFECTIVO"));
        mockMvc.perform(get("/api/ventas/11")).andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"total\":25.50,\"metodoPago\":\"TARJETA\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/ventas/12").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"total\":30,\"metodoPago\":\"EFECTIVO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idVenta").value(12));
        mockMvc.perform(delete("/api/ventas/11")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/ventas/99")).andExpect(status().isNotFound());

        verify(ventaDAO).save(any(VentaVO.class));
        verify(ventaDAO).update(any(VentaVO.class));
    }

    @Test
    void detalleVentaRestExponeCrudYRespuestasNotFound() throws Exception {
        DetalleVentaVO detalle = new DetalleVentaVO();
        detalle.setIdDetalle(13);
        detalle.setCantidad(2);
        when(detalleVentaDAO.findAll()).thenReturn(List.of(detalle));
        when(detalleVentaDAO.findById(13)).thenReturn(Optional.of(detalle));
        when(detalleVentaDAO.findById(99)).thenReturn(Optional.empty());
        when(detalleVentaDAO.save(any(DetalleVentaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(detalleVentaDAO.update(any(DetalleVentaVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(detalleVentaDAO.deleteById(13)).thenReturn(true);
        when(detalleVentaDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/detalle-ventas"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].cantidad").value(2));
        mockMvc.perform(get("/api/detalle-ventas/13")).andExpect(status().isOk());
        mockMvc.perform(get("/api/detalle-ventas/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/detalle-ventas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idVenta\":1,\"idProducto\":2,\"cantidad\":2,\"precioUnitario\":10}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/detalle-ventas/14").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idVenta\":1,\"idProducto\":2,\"cantidad\":3,\"precioUnitario\":10}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idDetalle").value(14));
        mockMvc.perform(delete("/api/detalle-ventas/13")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/detalle-ventas/99")).andExpect(status().isNotFound());

        verify(detalleVentaDAO).save(any(DetalleVentaVO.class));
        verify(detalleVentaDAO).update(any(DetalleVentaVO.class));
    }

    @Test
    void movimientoRestExponeCrudYRespuestasNotFound() throws Exception {
        MovimientoInventarioVO movimiento = new MovimientoInventarioVO();
        movimiento.setIdMovimiento(15);
        movimiento.setTipo("ENTRADA");
        when(movimientoDAO.findAll()).thenReturn(List.of(movimiento));
        when(movimientoDAO.findById(15)).thenReturn(Optional.of(movimiento));
        when(movimientoDAO.findById(99)).thenReturn(Optional.empty());
        when(movimientoDAO.save(any(MovimientoInventarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimientoDAO.update(any(MovimientoInventarioVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movimientoDAO.deleteById(15)).thenReturn(true);
        when(movimientoDAO.deleteById(99)).thenReturn(false);

        mockMvc.perform(get("/api/movimientos-inventario"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].tipo").value("ENTRADA"));
        mockMvc.perform(get("/api/movimientos-inventario/15")).andExpect(status().isOk());
        mockMvc.perform(get("/api/movimientos-inventario/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/movimientos-inventario").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":2,\"idUsuario\":1,\"tipo\":\"ENTRADA\",\"cantidad\":5}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/movimientos-inventario/16").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":2,\"idUsuario\":1,\"tipo\":\"AJUSTE\",\"cantidad\":3}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idMovimiento").value(16));
        mockMvc.perform(delete("/api/movimientos-inventario/15")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/movimientos-inventario/99")).andExpect(status().isNotFound());

        verify(movimientoDAO).save(any(MovimientoInventarioVO.class));
        verify(movimientoDAO).update(any(MovimientoInventarioVO.class));
    }
}
