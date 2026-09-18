package com.datagrada.backend.controller;

import com.datagrada.backend.model.Competicion;
import com.datagrada.backend.repository.CompeticionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/competiciones")
public class CompeticionController {

    private final CompeticionRepository competicionRepository;

    public CompeticionController(CompeticionRepository competicionRepository) {
        this.competicionRepository = competicionRepository;
    }

    @GetMapping
    public List<Competicion> obtenerTodas() {
        return competicionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Competicion obtenerPorId(@PathVariable Integer id) {
        return competicionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Competicion crearCompeticion(@RequestBody Competicion nueva) {
        return competicionRepository.save(nueva);
    }

    @PutMapping("/{id}")
    public Competicion actualizarCompeticion(@PathVariable Integer id, @RequestBody Competicion detalles) {
        Competicion existente = competicionRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombre(detalles.getNombre());
            existente.setTipo(detalles.getTipo());
            return competicionRepository.save(existente);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void borrarCompeticion(@PathVariable Integer id) {
        competicionRepository.deleteById(id);
    }
}