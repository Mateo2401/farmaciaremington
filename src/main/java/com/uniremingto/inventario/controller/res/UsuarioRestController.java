package com.uniremingto.inventario.controller.res;

import com.uniremingto.inventario.model.dto.UsuarioRespuestaDTO;
import com.uniremingto.inventario.model.vo.UsuarioVO;
import com.uniremingto.inventario.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Devuelve todos los usuarios. */
    @GetMapping
    public List<UsuarioRespuestaDTO> listarUsuarios() {
        return usuarioService.listar().stream().map(UsuarioRespuestaDTO::desde).toList();
    }

    /** Busca un usuario y responde 404 cuando no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> obtenerUsuario(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
                .map(UsuarioRespuestaDTO::desde)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crea un usuario y responde 201 con el registro creado. */
    @PostMapping
    public ResponseEntity<UsuarioRespuestaDTO> crearUsuario(@RequestBody UsuarioVO usuario) {
        try {
            UsuarioVO guardado = usuarioService.crear(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioRespuestaDTO.desde(guardado));
        } catch (DuplicateKeyException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /** Actualiza el usuario indicado por la ruta. */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> actualizarUsuario(@PathVariable Integer id,
                                                                  @RequestBody UsuarioVO usuario) {
        if (usuarioService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            UsuarioVO actualizado = usuarioService.actualizar(id, usuario);
            return ResponseEntity.ok(UsuarioRespuestaDTO.desde(actualizado));
        } catch (DuplicateKeyException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /** Elimina el usuario o responde 404 si no existía. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        try {
            boolean eliminado = usuarioService.eliminar(id);
            return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
