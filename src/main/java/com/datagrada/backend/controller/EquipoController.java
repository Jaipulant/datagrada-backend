package com.datagrada.backend.controller;

import com.datagrada.backend.model.Equipo;
import com.datagrada.backend.repository.EquipoRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping; // <-- Borrar en la BD
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // <-- Añadir en la BD
import org.springframework.web.bind.annotation.PutMapping; // <-- Actualizar en la BD
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // --- ¡LO NUEVO! ---

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
}