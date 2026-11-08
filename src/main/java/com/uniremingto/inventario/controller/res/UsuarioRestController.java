package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dao.UsuarioDAO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Expone operaciones REST para usuarios. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioRestController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** Devuelve todos los usuarios. */
    @GetMapping
    public List<UsuarioVO> listarUsuarios() {
        return usuarioDAO.findAll();
    }

    /** Busca un usuario y responde 404 cuando no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioVO> obtenerUsuario(@PathVariable Integer id) {
        return usuarioDAO.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crea un usuario y responde 201 con el registro creado. */
    @PostMapping
    public ResponseEntity<UsuarioVO> crearUsuario(@RequestBody UsuarioVO usuario) {
        UsuarioVO guardado = usuarioDAO.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /** Actualiza el usuario indicado por la ruta. */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioVO> actualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioVO usuario) {
        usuario.setIdUsuario(id);
        UsuarioVO actualizado = usuarioDAO.update(usuario);
        return ResponseEntity.ok(actualizado);
    }

    /** Elimina el usuario o responde 404 si no existía. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        boolean eliminado = usuarioDAO.deleteById(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
