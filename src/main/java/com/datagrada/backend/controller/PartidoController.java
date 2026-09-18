package com.datagrada.backend.controller;

import com.datagrada.backend.model.Partido;
import com.datagrada.backend.model.Usuario;
import com.datagrada.backend.repository.PartidoRepository;
import com.datagrada.backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    // Método auxiliar para obtener el username del token actual
    private String obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Devuelve el username guardado en el JWT
    }

    // 1. LEER TODOS (Solo los del usuario logueado)
    @GetMapping
    public List<Partido> obtenerTodosLosPartidos() {
        String username = obtenerUsuarioActual();
        return partidoRepository.findByUsuarioUsername(username); 
    }

    // 2. LEER UNO POR ID 
    @GetMapping("/{id}")
    public Partido obtenerPartidoPorId(@PathVariable Integer id) {
        return partidoRepository.findById(id).orElse(null);
    }

    // 3. CREAR (Asociándolo al usuario del token)
    @PostMapping
    public Partido crearPartido(@RequestBody Partido nuevoPartido) {
        String username = obtenerUsuarioActual();
        
        // Buscamos tu entidad Usuario y se la asignamos al partido para que no se quede sin dueño
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username).orElse(null);
        nuevoPartido.setUsuario(usuarioLogueado);

        return partidoRepository.save(nuevoPartido);
    }

    // 4. ACTUALIZAR (PUT)
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

    // 5. BORRAR (DELETE)
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
}