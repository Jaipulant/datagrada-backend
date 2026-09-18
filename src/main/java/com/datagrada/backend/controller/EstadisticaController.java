package com.datagrada.backend.controller;

import com.datagrada.backend.model.Partido;
import com.datagrada.backend.model.Usuario;
import com.datagrada.backend.repository.PartidoRepository;
import com.datagrada.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticaController {

    private final PartidoRepository partidoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstadisticaController(PartidoRepository partidoRepository, UsuarioRepository usuarioRepository) {
        this.partidoRepository = partidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Método para saber quién está haciendo la petición
    private String obtenerUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/termometro")
    public ResponseEntity<Map<String, Object>> obtenerTermometro() {
        String username = obtenerUsuarioActual();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        // Si el usuario no existe o aún no ha elegido equipo favorito, devolvemos un 404 amigable
        if (usuarioOpt.isEmpty() || usuarioOpt.get().getEquipoFavorito() == null) {
            return ResponseEntity.notFound().build();
        }

        Integer idEquipo = usuarioOpt.get().getEquipoFavorito().getIdEquipos();
        String nombreEquipo = usuarioOpt.get().getEquipoFavorito().getNombre();
        String urlEscudo = usuarioOpt.get().getEquipoFavorito().getUrlEscudo();
        
        // ¡Usamos la consulta personalizada que ya tenías creada en tu repositorio!
        List<Partido> partidos = partidoRepository.buscarPorUsuarioYEquipo(username, idEquipo);

        int victorias = 0;
        int empates = 0;
        int derrotas = 0;

        // La magia: calculamos el resultado partido a partido
        for (Partido p : partidos) {
            boolean esLocal = p.getEquipoLocal().getIdEquipos().equals(idEquipo);
            
            int golesFavor = esLocal ? p.getGolesLocal() : p.getGolesVisitante();
            int golesContra = esLocal ? p.getGolesVisitante() : p.getGolesLocal();

            if (golesFavor > golesContra) {
                victorias++;
            } else if (golesFavor < golesContra) {
                derrotas++;
            } else {
                empates++;
            }
        }

        // Empaquetamos todo en un JSON limpio para el frontend
        Map<String, Object> stats = new HashMap<>();
        stats.put("equipo", nombreEquipo);
        stats.put("escudo", urlEscudo);
        stats.put("partidosJugados", partidos.size());
        stats.put("victorias", victorias);
        stats.put("empates", empates);
        stats.put("derrotas", derrotas);

        return ResponseEntity.ok(stats);
    }
}