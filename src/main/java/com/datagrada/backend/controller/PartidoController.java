package com.datagrada.backend.controller;

import com.datagrada.backend.model.Partido;
import com.datagrada.backend.model.Usuario;
import com.datagrada.backend.repository.PartidoRepository;
import com.datagrada.backend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoRepository partidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PartidoController(PartidoRepository partidoRepository, UsuarioRepository usuarioRepository) {
        this.partidoRepository = partidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private String obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); 
    }

    @GetMapping
    public List<Partido> obtenerTodosLosPartidos() {
        String username = obtenerUsuarioActual();
        return partidoRepository.findByUsuarioUsername(username); 
    }

    @GetMapping("/{id}")
    public Partido obtenerPartidoPorId(@PathVariable Integer id) {
        return partidoRepository.findById(id).orElse(null);
    }

    // ACTUALIZADO: Manejo de errores y prevención de duplicados
    @PostMapping
    public ResponseEntity<?> crearPartido(@RequestBody Partido nuevoPartido) {
        String username = obtenerUsuarioActual();
        
        // 1. Verificar si el partido ya existe para este usuario ese mismo día
        boolean existeDuplicado = partidoRepository.existsByUsuarioUsernameAndFechaAndEquipoLocal_IdEquiposAndEquipoVisitante_IdEquipos(
                username, 
                nuevoPartido.getFecha(), 
                nuevoPartido.getEquipoLocal().getIdEquipos(), 
                nuevoPartido.getEquipoVisitante().getIdEquipos()
        );

        if (existeDuplicado) {
            // El status 400 disparará el bloque "else" en el fetch de tu frontend
            return ResponseEntity.badRequest().body("Ya tienes registrado este mismo enfrentamiento en esa fecha.");
        }
        
        // 2. Si no existe, lo guardamos normalmente
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username).orElse(null);
        nuevoPartido.setUsuario(usuarioLogueado);

        Partido partidoGuardado = partidoRepository.save(nuevoPartido);
        return ResponseEntity.ok(partidoGuardado);
    }

    @PutMapping("/{id}")
    public Partido actualizarPartido(@PathVariable Integer id, @RequestBody Partido detallesPartido) {
        Partido partidoExistente = partidoRepository.findById(id).orElse(null);
        
        if (partidoExistente != null) {
            partidoExistente.setFecha(detallesPartido.getFecha());
            partidoExistente.setJornada(detallesPartido.getJornada());
            partidoExistente.setGolesLocal(detallesPartido.getGolesLocal());
            partidoExistente.setGolesVisitante(detallesPartido.getGolesVisitante());
            partidoExistente.setEquipoLocal(detallesPartido.getEquipoLocal());
            partidoExistente.setEquipoVisitante(detallesPartido.getEquipoVisitante());
            partidoExistente.setCompeticion(detallesPartido.getCompeticion());
            
            return partidoRepository.save(partidoExistente);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void borrarPartido(@PathVariable Integer id) {
        partidoRepository.deleteById(id);
    }

    // --- FILTROS ADAPTADOS AL USUARIO ---

    @GetMapping("/fecha/{fecha}")
    public List<Partido> obtenerPorFecha(@PathVariable LocalDate fecha) {
        String username = obtenerUsuarioActual();
        return partidoRepository.findByUsuarioUsernameAndFecha(username, fecha);
    }

    @GetMapping("/competicion/{idCompeticion}")
    public List<Partido> obtenerPorCompeticion(@PathVariable Integer idCompeticion) {
        String username = obtenerUsuarioActual();
        return partidoRepository.findByUsuarioUsernameAndCompeticion_IdCompeticion(username, idCompeticion);
    }

    @GetMapping("/competicion/{idCompeticion}/jornada/{jornada}")
    public List<Partido> obtenerPorCompeticionYJornada(
            @PathVariable Integer idCompeticion, 
            @PathVariable String jornada) {
        String username = obtenerUsuarioActual();
        return partidoRepository.findByUsuarioUsernameAndJornadaAndCompeticion_IdCompeticion(username, jornada, idCompeticion);
    }

    @GetMapping("/equipo/{idEquipo}")
    public List<Partido> obtenerPorEquipo(@PathVariable Integer idEquipo) {
        String username = obtenerUsuarioActual();
        return partidoRepository.buscarPorUsuarioYEquipo(username, idEquipo);
    }

    // --- NUEVO ENDPOINT PARA EL WRAPPED DE FINAL DE AÑO ---
    @GetMapping("/stats/resumen/{anio}")
    public ResponseEntity<Map<String, Object>> obtenerResumenAnual(@PathVariable int anio) {
        String username = obtenerUsuarioActual();
        
        long totalPartidos = partidoRepository.countPartidosByAnio(username, anio);
        List<Object[]> topCompeticiones = partidoRepository.findCompeticionesMasVistas(username, anio);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("anio", anio);
        stats.put("totalPartidosVistos", totalPartidos);
        stats.put("competicionesFavoritas", topCompeticiones);
        
        return ResponseEntity.ok(stats);
    }
}