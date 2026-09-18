package com.datagrada.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Esta es la llave maestra de tu servidor. Solo él puede fabricar y leer tokens válidos.
    // (En un entorno real de producción, esto se guarda oculto en las variables de entorno, pero para empezar lo generamos aquí)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Método para fabricar el token cuando el usuario hace login correctamente
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // El token caduca a las 24 horas (1000 ms * 60 s * 60 min * 24 h)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) 
                .signWith(SECRET_KEY)
                .compact();
    }

    // Método para leer el nombre de usuario que está escondido dentro del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Comprueba si el token es del usuario que dice ser y si no ha caducado
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}