package com.datagrada.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "partidos")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partidos")
    private Integer idPartidos;
    
    private LocalDate fecha; 

    // ¡Aquí está la columna que añadiste por tu cuenta!
    private String jornada;

    @Column(name = "goles_local")
    private Integer golesLocal;

    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    @ManyToOne
    @JoinColumn(name = "id_equipo_local")
    private Equipo equipoLocal;

    @ManyToOne
    @JoinColumn(name = "id_equipo_visitante")
    private Equipo equipoVisitante;

    @ManyToOne
    @JoinColumn(name = "id_competicion")
    private Competicion competicion;

    // --- Nueva relación con el dueño del partido ---
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Partido() {}

    // --- Getters y Setters ---

    public Integer getIdPartidos() { return idPartidos; }
    public void setIdPartidos(Integer idPartidos) { this.idPartidos = idPartidos; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }

    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }

    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }

    public Competicion getCompeticion() { return competicion; }
    public void setCompeticion(Competicion competicion) { this.competicion = competicion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}