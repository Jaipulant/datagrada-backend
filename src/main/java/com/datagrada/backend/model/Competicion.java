package com.datagrada.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "competiciones")
public class Competicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competicion")
    private Integer idCompeticion;

    private String nombre;

    private String tipo;

    public Competicion() {}

    // --- Getters y Setters ---

    public Integer getIdCompeticion() { return idCompeticion; }
    public void setIdCompeticion(Integer idCompeticion) { this.idCompeticion = idCompeticion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}