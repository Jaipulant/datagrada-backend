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

    // 6. BUSCAR EXTERNO (PROXY A THESPORTSDB CON MÁSCARA)
    @GetMapping("/buscar-externo")
    public org.springframework.http.ResponseEntity<String> buscarEquipoExterno(@RequestParam String nombre) {
        try {
            String url = "https://www.thesportsdb.com/api/v1/json/1/searchteams.php?t=" 
                    + java.net.URLEncoder.encode(nombre, "UTF-8");
            
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            
            // MÁSCARA: Engañamos a TheSportsDB haciéndonos pasar por un navegador Google Chrome de Windows
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            
            // Hacemos la petición enviando nuestras cabeceras falsas
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                String.class
            );
            
            return org.springframework.http.ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            // Imprimimos el error exacto en los logs de Render para saber qué pasa si nos bloquean la IP
            System.out.println("❌ ERROR THESPORTSDB: " + e.getMessage());
            return org.springframework.http.ResponseEntity.status(500).body("{\"error\": \"Error al consultar API externa\"}");
        }
    }
}