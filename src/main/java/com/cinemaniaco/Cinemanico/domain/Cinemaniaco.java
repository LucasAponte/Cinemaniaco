package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String apodo;
    @OneToMany
    private List<Pelicula> peliculas = new ArrayList<>();
    @ManyToMany
    private List<Cinemaniaco> amigos = new ArrayList<>();
    @ManyToMany(mappedBy = "amigos")
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
        return this.amigos.contains(cinemaniacoASeguir);
    }

    private void agregarSeguidor(Cinemaniaco seguidor) {
        if (this.seguidores == null) this.seguidores = new ArrayList<>();
        if (!this.seguidores.contains(seguidor)) this.seguidores.add(seguidor);
    }

    private boolean puedeSegir(Cinemaniaco cinemaniacoASeguir) {
        return !esMismoUsuario(cinemaniacoASeguir) && !yaLoSigue(cinemaniacoASeguir);
    }

    private List<Cinemaniaco> getAmigosSeguro() {
        return this.amigos != null ? this.amigos : new ArrayList<>();
    }

    private List<Cinemaniaco> getSeguidoresSeguro() {
        return this.seguidores != null ? this.seguidores : new ArrayList<>();
    }

    // ─── Métodos principales ────────────────────────────────────────────────

    public boolean seguir(Cinemaniaco cinemaniacoASeguir) {
        validarNoNull(cinemaniacoASeguir, "El objetivo no puede ser null");
        if (!puedeSegir(cinemaniacoASeguir)) return false;

        this.amigos.add(cinemaniacoASeguir);
        cinemaniacoASeguir.agregarSeguidor(this);
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

        Set<Cinemaniaco> conjunto = new HashSet<>(getAmigosSeguro());
        conjunto.retainAll(otro.getAmigosSeguro());
        return new ArrayList<>(conjunto);
    }
}