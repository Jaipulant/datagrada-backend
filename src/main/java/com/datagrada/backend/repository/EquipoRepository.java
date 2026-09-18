package com.datagrada.backend.repository;

import com.datagrada.backend.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    
    // ¡Sorpresa! Está vacío por dentro.
    // Al heredar (extends) de JpaRepository, Spring Boot nos regala 
    // todos los métodos para guardar, buscar, borrar... sin escribir una sola línea de código SQL.
}