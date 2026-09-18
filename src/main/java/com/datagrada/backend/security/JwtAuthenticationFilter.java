package com.datagrada.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Si no hay cabecera o no empieza por "Bearer ", dejamos que la petición continúe.
        // Si la ruta era privada (como el historial), el SecurityConfig la bloqueará en el siguiente paso.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token quitando la palabra "Bearer " (los primeros 7 caracteres)
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // Si el token tiene un nombre de usuario y el usuario aún no está validado en el sistema
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Buscamos al usuario en la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Si el token es válido y no ha caducado
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                
                // Creamos el "pase VIP"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Le decimos a Spring que este usuario ya puede pasar
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continuamos con el resto de filtros de la aplicación
        filterChain.doFilter(request, response);
    }
}