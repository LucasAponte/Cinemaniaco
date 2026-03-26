package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cinemaniaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id_Cinemaniaco;

    @OneToOne
    private Persona persona;
    @EqualsAndHashCode.Include
    private String apodo;

    @OneToMany
    private List<Pelicula> peliculas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "cinemaniaco_seguidos",
            joinColumns = @JoinColumn(name = "seguidor_id"),
            inverseJoinColumns = @JoinColumn(name = "seguido_id")
    )
    private List<Cinemaniaco> seguidos = new ArrayList<>();

    @ManyToMany(mappedBy = "seguidos")
    private List<Cinemaniaco> seguidores = new ArrayList<>();

    public Cinemaniaco(Persona persona, String apodo) {
        this.persona = persona;
        this.apodo = apodo;
    }

    // ─── Auxiliares privadas ────────────────────────────────────────────────

    private void validarNoNull(Cinemaniaco cinemaniacoASeguir, String mensaje) {
        if (cinemaniacoASeguir == null) throw new IllegalArgumentException(mensaje);
    }

    private boolean esMismoUsuario(Cinemaniaco cinemaniacoASeguir) {
        return this.equals(cinemaniacoASeguir);
    }

    private boolean yaLoSigue(Cinemaniaco cinemaniacoASeguir) {
        return this.seguidos.contains(cinemaniacoASeguir);
    }

    private void agregarSeguidor(Cinemaniaco seguidor) {
        if (this.seguidores == null) this.seguidores = new ArrayList<>();
        if (!this.seguidores.contains(seguidor)) this.seguidores.add(seguidor);
    }

    private boolean puedeSeguir(Cinemaniaco cinemaniacoASeguir) {
        return !esMismoUsuario(cinemaniacoASeguir) && !yaLoSigue(cinemaniacoASeguir);
    }

    private List<Cinemaniaco> getSeguidsSeguro() {
        return this.seguidos != null ? this.seguidos : new ArrayList<>();
    }

    private List<Cinemaniaco> getSeguidoresSeguro() {
        return this.seguidores != null ? this.seguidores : new ArrayList<>();
    }

    // ─── Métodos principales ────────────────────────────────────────────────

    public boolean seguir(Cinemaniaco cinemaniacoASeguir) {
        validarNoNull(cinemaniacoASeguir, "El objetivo no puede ser null");
        if (!puedeSeguir(cinemaniacoASeguir)) return false;

        this.seguidos.add(cinemaniacoASeguir);
        cinemaniacoASeguir.agregarSeguidor(this);
        return true;
    }

    public boolean dejarDeSeguir(Cinemaniaco cinemaniacoADejarDeSeguir) {
        if (cinemaniacoADejarDeSeguir == null) return false;
        if (!yaLoSigue(cinemaniacoADejarDeSeguir)) return false;

        this.seguidos.remove(cinemaniacoADejarDeSeguir);
        cinemaniacoADejarDeSeguir.getSeguidores().remove(this);
        return true;
    }

    public int contarSeguidores() {
        return getSeguidoresSeguro().size();
    }

    public boolean esSeguidor(Cinemaniaco posibleSeguidor) {
        if (posibleSeguidor == null) return false;
        return getSeguidoresSeguro().contains(posibleSeguidor);
    }

    public List<Cinemaniaco> amigosEnComun(Cinemaniaco otro) {
        if (otro == null) return new ArrayList<>();
        Set<Cinemaniaco> conjunto = new HashSet<>(getSeguidsSeguro());
        conjunto.retainAll(otro.getSeguidsSeguro());
        return new ArrayList<>(conjunto);
    }
}