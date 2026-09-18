package com.datagrada.backend.repository;

import com.datagrada.backend.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    // 1. Filtrar por Fecha exacta
    List<Partido> findByFecha(LocalDate fecha);

    // 2. Filtrar por Competición
    List<Partido> findByCompeticion_IdCompeticion(Integer idCompeticion);

    // 3. ¡El Super Filtro! Jornada Y Competición a la vez
    List<Partido> findByJornadaAndCompeticion_IdCompeticion(String jornada, Integer idCompeticion);

    // 4. Filtrar por Equipo (Ya sea que jugó de Local o de Visitante)
    @Query("SELECT p FROM Partido p WHERE p.equipoLocal.idEquipos = :idEquipo OR p.equipoVisitante.idEquipos = :idEquipo")
    List<Partido> buscarPorEquipo(@Param("idEquipo") Integer idEquipo);

    List<Partido> findByUsuarioUsername(String username);
    List<Partido> findByUsuarioUsernameAndFecha(String username, LocalDate fecha);
    List<Partido> findByUsuarioUsernameAndCompeticion_IdCompeticion(String username, Integer idCompeticion);
    List<Partido> findByUsuarioUsernameAndJornadaAndCompeticion_IdCompeticion(String username, String jornada, Integer idCompeticion);

    // Consulta personalizada para buscar por Usuario Y Equipo (Local o Visitante) adaptada a tu modelo "Usuario"
    @Query("SELECT p FROM Partido p WHERE p.usuario.username = :username AND (p.equipoLocal.idEquipos = :idEquipo OR p.equipoVisitante.idEquipos = :idEquipo)")
    List<Partido> buscarPorUsuarioYEquipo(@Param("username") String username, @Param("idEquipo") Integer idEquipo);

}