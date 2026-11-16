package com.uniremingto.inventario.controller.mvc;

import com.uniremingto.inventario.model.dao.ProductoDAO;
import com.uniremingto.inventario.model.vo.ProductoVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Atiende el ciclo de vida de productos a través de vistas Thymeleaf. */
@Controller
@RequestMapping("/productos")
public class ProductoMvcController {

    private final ProductoDAO productoDAO;

    public ProductoMvcController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    /** Carga desde el DAO los productos que se muestran en la lista. */
    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoDAO.findAll());
        return "productos/lista";
    }

    /** Prepara valores iniciales para el formulario de alta de producto. */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        ProductoVO producto = new ProductoVO();
        producto.setStock(0);
        producto.setStockMinimo(5);
        producto.setEstado(true);
        model.addAttribute("producto", producto);
        return "productos/formulario";
    }

    /**
        * Valida y persiste un producto nuevo.
        *
     * @param producto datos recibidos del formulario
     * @param bindingResult errores de conversión de campos enviados
     * @param model modelo de la vista para errores de persistencia
     * @param redirectAttributes mensajes que se muestran después de redirigir
     * @return vista del formulario si hay error o redirección al listado
     */
    @PostMapping
    public String guardarProducto(@ModelAttribute ProductoVO producto, BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        return persistirProducto(producto, bindingResult, model, redirectAttributes, false);
    }

    /**
        * Asigna el ID de la ruta y actualiza el producto existente.
        *
     * @param id identificador del producto que se actualiza
     * @param producto datos enviados por el formulario
     * @param bindingResult errores de conversión de campos enviados
     * @param model modelo de la vista para errores de persistencia
     * @param redirectAttributes mensajes que se muestran después de redirigir
     * @return vista del formulario si hay error o redirección al listado
     */
    @PostMapping("/{id}")
    public String actualizarProducto(@PathVariable Integer id, @ModelAttribute ProductoVO producto,
                                     BindingResult bindingResult, Model model,
                                     RedirectAttributes redirectAttributes) {
        producto.setIdProducto(id);
        return persistirProducto(producto, bindingResult, model, redirectAttributes, true);
    }

    private String persistirProducto(ProductoVO producto, BindingResult bindingResult, Model model,
                                     RedirectAttributes redirectAttributes, boolean edicion) {
        if (producto.getIdCategoria() == null) {
            bindingResult.rejectValue("idCategoria", "required", "Selecciona el ID de una categoría.");
        }
        if (producto.getPrecioCompra() == null || producto.getPrecioCompra().signum() < 0) {
            bindingResult.rejectValue("precioCompra", "invalid", "Ingresa un precio de compra igual o mayor que cero.");
        }
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().signum() < 0) {
            bindingResult.rejectValue("precioVenta", "invalid", "Ingresa un precio de venta igual o mayor que cero.");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            bindingResult.rejectValue("stock", "invalid", "El stock debe ser igual o mayor que cero.");
        }
        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            bindingResult.rejectValue("stockMinimo", "invalid", "El stock mínimo debe ser igual o mayor que cero.");
        }
        if (bindingResult.hasErrors()) {
            return "productos/formulario";
        }

        try {
            if (edicion) {
                productoDAO.update(producto);
            } else {
                productoDAO.save(producto);
            }
        } catch (DuplicateKeyException exception) {
            model.addAttribute("formError", "Ya existe otro producto con ese código.");
            return "productos/formulario";
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("formError", "Verifica que la categoría y el proveedor existan y que los datos sean válidos.");
            return "productos/formulario";
        }

        redirectAttributes.addFlashAttribute("mensaje",
                edicion ? "El producto fue actualizado." : "El producto fue guardado.");
        return "redirect:/productos";
    }

    /** Carga un producto existente para editarlo. */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        productoDAO.findById(id).ifPresent(producto -> model.addAttribute("producto", producto));
        return "productos/formulario";
    }

    /**
        * Elimina un producto si no está referenciado por el historial.
        *
     * @param id identificador del producto que se elimina
     * @param redirectAttributes resultado de la operación para la lista
     * @return redirección a la lista de productos
     */
    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (productoDAO.deleteById(id)) {
                redirectAttributes.addFlashAttribute("mensaje", "El producto fue eliminado.");
            } else {
                redirectAttributes.addFlashAttribute("error", "El producto ya no existe.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el producto porque está asociado a ventas o movimientos de inventario.");
        }
        return "redirect:/productos";
    }
}
