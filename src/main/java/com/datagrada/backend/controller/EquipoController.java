package com.datagrada.backend.controller;

import com.datagrada.backend.model.Equipo;
import com.datagrada.backend.repository.EquipoRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoRepository equipoRepository;

    public EquipoController(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    // 1. LEER TODOS (GET)
    @GetMapping
    public List<Equipo> obtenerTodosLosEquipos() {
        return equipoRepository.findAll(); 
    }

    // 2. LEER UNO (GET)
    @GetMapping("/{id}")
    public Equipo obtenerEquipoPorId(@PathVariable Integer id) {
        return equipoRepository.findById(id).orElse(null);
    }

    // 3. CREAR (POST)
    @PostMapping
    public Equipo crearEquipo(@RequestBody Equipo nuevoEquipo) {
        return equipoRepository.save(nuevoEquipo);
    }

    // 4. ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public Equipo actualizarEquipo(@PathVariable Integer id, @RequestBody Equipo detallesEquipo) {
        // Primero buscamos si el equipo existe
        Equipo equipoExistente = equipoRepository.findById(id).orElse(null);
        
        if (equipoExistente != null) {
            // Modificamos los datos
            equipoExistente.setNombre(detallesEquipo.getNombre());
            equipoExistente.setPais(detallesEquipo.getPais());
            equipoExistente.setEstadio(detallesEquipo.getEstadio());
            
            // Guardamos los cambios
            return equipoRepository.save(equipoExistente);
        }
        return null;
    }

    // 5. BORRAR (DELETE)
    @DeleteMapping("/{id}")
    public void borrarEquipo(@PathVariable Integer id) {
        equipoRepository.deleteById(id);
    }

    // 6. BUSCAR EXTERNO (PROXY A THESPORTSDB)
    @GetMapping("/buscar-externo")
    public ResponseEntity<String> buscarEquipoExterno(@RequestParam String nombre) {
        try {
            String url = "https://www.thesportsdb.com/api/v1/json/1/searchteams.php?t=" 
                    + URLEncoder.encode(nombre, StandardCharsets.UTF_8);
            RestTemplate restTemplate = new RestTemplate();
            String jsonResultado = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(jsonResultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error al consultar API externa\"}");
        }
    }
}