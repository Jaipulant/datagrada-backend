package com.datagrada.backend.repository;

import com.datagrada.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Este método es clave: Spring Boot lo usará automáticamente para buscar si el usuario existe en la BD cuando intente hacer login
    Optional<Usuario> findByUsername(String username);
}