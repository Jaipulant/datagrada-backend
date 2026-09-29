package com.datagrada.backend.repository;

import com.datagrada.backend.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    // --- FILTROS BÁSICOS ---
    List<Partido> findByFecha(LocalDate fecha);
    List<Partido> findByCompeticion_IdCompeticion(Integer idCompeticion);
    List<Partido> findByJornadaAndCompeticion_IdCompeticion(String jornada, Integer idCompeticion);
    
    @Query("SELECT p FROM Partido p WHERE p.equipoLocal.idEquipos = :idEquipo OR p.equipoVisitante.idEquipos = :idEquipo")
    List<Partido> buscarPorEquipo(@Param("idEquipo") Integer idEquipo);

    // --- FILTROS POR USUARIO ---
    List<Partido> findByUsuarioUsername(String username);
    List<Partido> findByUsuarioUsernameAndFecha(String username, LocalDate fecha);
    List<Partido> findByUsuarioUsernameAndCompeticion_IdCompeticion(String username, Integer idCompeticion);
    List<Partido> findByUsuarioUsernameAndJornadaAndCompeticion_IdCompeticion(String username, String jornada, Integer idCompeticion);

    @Query("SELECT p FROM Partido p WHERE p.usuario.username = :username AND (p.equipoLocal.idEquipos = :idEquipo OR p.equipoVisitante.idEquipos = :idEquipo)")
    List<Partido> buscarPorUsuarioYEquipo(@Param("username") String username, @Param("idEquipo") Integer idEquipo);

    // --- NIVEL 3: VALIDACIÓN ANTI-DUPLICADOS ---
    // Comprueba si ya existe este mismo partido registrado por el usuario en la misma fecha
    boolean existsByUsuarioUsernameAndFechaAndEquipoLocal_IdEquiposAndEquipoVisitante_IdEquipos(
            String username, LocalDate fecha, Integer idLocal, Integer idVisitante);

    // --- NIVEL 3: ESTADÍSTICAS PARA EL "WRAPPED" ---
    
    // 1. Total de partidos vistos en un año concreto por el usuario
    @Query("SELECT COUNT(p) FROM Partido p WHERE p.usuario.username = :username AND YEAR(p.fecha) = :anio")
    long countPartidosByAnio(@Param("username") String username, @Param("anio") int anio);

    // 2. Ranking de competiciones más vistas en un año
    @Query("SELECT p.competicion.nombre, COUNT(p) as total FROM Partido p WHERE p.usuario.username = :username AND YEAR(p.fecha) = :anio GROUP BY p.competicion.nombre ORDER BY total DESC")
    List<Object[]> findCompeticionesMasVistas(@Param("username") String username, @Param("anio") int anio);
}