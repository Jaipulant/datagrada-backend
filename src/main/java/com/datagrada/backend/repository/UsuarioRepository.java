package com.datagrada.backend.repository;

import com.datagrada.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Añade esta línea para que Spring reconozca el existsByUsername
    boolean existsByUsername(String username);

    Optional<Usuario> findByUsername(String username);
}