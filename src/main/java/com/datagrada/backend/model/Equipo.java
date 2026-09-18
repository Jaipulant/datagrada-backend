package com.datagrada.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipos")
    private Integer idEquipos;

    @Column(unique = true)
    private String nombre;
    private String pais;
    private String estadio;
    private String urlEscudo;

    // Constructor vacío (Obligatorio para que Spring Boot funcione)
    public Equipo() {}

    // --- Getters y Setters (Para leer y escribir los datos) ---

    public Integer getIdEquipos() { return idEquipos; }
    public void setIdEquipos(Integer idEquipos) { this.idEquipos = idEquipos; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstadio() { return estadio; }
    public void setEstadio(String estadio) { this.estadio = estadio; }

    public String getUrlEscudo() { return urlEscudo; }
    public void setUrlEscudo(String urlEscudo) { this.urlEscudo = urlEscudo; }
}