package com.datagrada.backend.controller;

import com.datagrada.backend.model.Equipo;
import com.datagrada.backend.model.Usuario;
import com.datagrada.backend.repository.EquipoRepository;
import com.datagrada.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;

    // Este es el constructor que Java te está pidiendo para inicializar los "final"
    public UsuarioController(UsuarioRepository usuarioRepository, EquipoRepository equipoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
    }

    // Método auxiliar para saber quién está logueado por el Token
    private String obtenerUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // 1. Obtener los datos del usuario actual (incluyendo su equipo favorito)
    @GetMapping("/me")
    public ResponseEntity<Usuario> obtenerMiPerfil() {
        String username = obtenerUsuarioActual();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        return usuarioOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 2. Guardar o cambiar el equipo favorito
    @PutMapping("/me/equipo-favorito/{idEquipo}")
    public ResponseEntity<String> actualizarEquipoFavorito(@PathVariable Integer idEquipo) {
        String username = obtenerUsuarioActual();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        Optional<Equipo> equipoOpt = equipoRepository.findById(idEquipo);

        if (usuarioOpt.isPresent() && equipoOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEquipoFavorito(equipoOpt.get());
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Equipo favorito actualizado con éxito");
        }
        return ResponseEntity.badRequest().body("Usuario o equipo no encontrado");
    }
}