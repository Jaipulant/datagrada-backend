package com.datagrada.backend.controller;

import com.datagrada.backend.model.Equipo;
import com.datagrada.backend.model.Usuario;
import com.datagrada.backend.repository.EquipoRepository;
import com.datagrada.backend.repository.UsuarioRepository;
import com.datagrada.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository; // Añadido para asignar el equipo por defecto
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, 
                          UsuarioRepository usuarioRepository, 
                          EquipoRepository equipoRepository,
                          PasswordEncoder passwordEncoder, 
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping({"/register", "/registro"})
    public ResponseEntity<?> registrar(@RequestBody AuthRequest request) {
        // Comprobamos si el nombre de usuario ya existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }

        // Creamos el usuario y encriptamos su contraseña con BCrypt
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Asignación por defecto: Nuestro equipo favorito absoluto (Manchester City)
        Equipo manCity = equipoRepository.findByNombreIgnoreCase("Manchester City").orElse(null);
        if (manCity != null) {
            nuevoUsuario.setEquipoFavorito(manCity);
        }
        
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Lanza la comprobación de seguridad. Si la contraseña falla, corta aquí.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Si es correcto, generamos el Token JWT
        String token = jwtService.generateToken(request.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        
        return ResponseEntity.ok(response);
    }

    // Endpoint ultraligero y público para UptimeRobot
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    // Clase auxiliar estática para recibir los datos
    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}