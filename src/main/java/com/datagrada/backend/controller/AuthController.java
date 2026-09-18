package com.datagrada.backend.controller;

import com.datagrada.backend.model.Usuario;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, 
                          UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody AuthRequest request) {
        // Comprobamos si el nombre estilo "joseperez_" ya está pillado
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        // Creamos el usuario y encriptamos su contraseña antes de guardarla en la base de datos
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Esta línea lanza la comprobación de seguridad. Si la contraseña es incorrecta, corta la ejecución aquí.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Si la contraseña es correcta, fabricamos su carnet digital (Token JWT)
        String token = jwtService.generateToken(request.getUsername());
        
        // Devolvemos el token en un formato JSON limpio para que tu JavaScript lo guarde fácilmente
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        
        return ResponseEntity.ok(response);
    }

    // Clase auxiliar estática para recibir los datos desde el frontend cómodamente
    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}