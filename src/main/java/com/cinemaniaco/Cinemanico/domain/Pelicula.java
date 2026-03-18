package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Pelicula;

    private String titulo;
    private String director;
    private int anioEstreno;

    @ElementCollection
    private List<String> actores;

    @ElementCollection
    private List<String> generos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pelicula_id")
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pelicula_id")
    private List<Puntuacion> puntuaciones = new ArrayList<>();

    //Debería cambiar el nombre a comentario IA?
    @Column(columnDefinition = "TEXT")
    private String resumenIA;

    public Pelicula(String titulo, String director, int anioEstreno, List<String> actores, List<String> generos) {
        this.titulo = titulo;
        this.director = director;
        this.anioEstreno = anioEstreno;
        this.actores = actores;
        this.generos = generos;
    }

    public void anadirPuntuacion(Cinemaniaco cinemaniaco, double puntos) {
        Puntuacion puntuacion = buscarPuntuacionPorCinemaniaco(cinemaniaco);
        if (puntuacion != null) {
            puntuacion.setPuntuacion(puntos);
        } else {
            this.puntuaciones.add(new Puntuacion(cinemaniaco, puntos));
        }
    }

    public int cantPuntuaciones() {
        return puntuaciones.size();
    }

    public double calcularPuntuacionPromedio() {
        if (puntuaciones.isEmpty()) return 0.0;
        return puntuaciones.stream()
                .mapToDouble(Puntuacion::getPuntuacion)
                .average()
                .orElse(0.0);
    }

    public Puntuacion buscarPuntuacionPorCinemaniaco(Cinemaniaco cinemaniaco) {
        return this.puntuaciones.stream()
                .filter(p -> p.getCinemaniaco().equals(cinemaniaco))
                .findFirst()
                .orElse(null);
    }

    public void anadirComentario(Comentario comentario) {
        comentarios.add(comentario);
    }

    public Comentario buscarComentarioDe(Cinemaniaco cinemaniaco) {
        return this.comentarios.stream()
                .filter(c -> c.getCinemaniaco().equals(cinemaniaco))
                .findFirst()
                .orElse(null);
    }
}