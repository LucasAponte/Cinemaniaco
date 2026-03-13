package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Pelicula {
    @Id
    private Long id_Pelicula;
    private String titulo;
    private String director;
    private int anioEstreno;
    private List<String> actores;
    private List<String> generos;

    //Cosas de funcionalidad
    @OneToMany
    private List<Comentario> comentarios = new java.util.ArrayList<>();
    @OneToOne
    private Comentario comentarioEnComun;
    @OneToMany
    private List<Puntuacion> puntuaciones = new java.util.ArrayList<>();

    public Pelicula(String titulo, String director, int anioEstreno, List<String> actores, List<String> generos) {
        this.titulo = titulo;
        this.director = director;
        this.anioEstreno = anioEstreno;
        this.actores = actores;
        this.generos = generos;
    }

    public void anadirPuntuacion(Cinemaniaco cinemaniaco, double puntos) {
        Puntuacion puntuacion = buscarPuntuancionPorCinemaniaco(cinemaniaco);
        if(puntuacion != null){
           puntuacion.setPuntuacion(puntos);
        }else{
            Puntuacion nuevaPuntuacion = new Puntuacion();
            nuevaPuntuacion.setCinemaniaco(cinemaniaco);
            nuevaPuntuacion.setPuntuacion(puntos);
            puntuaciones.add(nuevaPuntuacion);
        }
    }

    public int cantPuntuaciones() {
        return puntuaciones.size();
    }

    public double calcularPuntuacionPromedio() {
        if (puntuaciones.isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        for (Puntuacion p : puntuaciones) {
            suma += p.getPuntuacion();
        }
        return suma / puntuaciones.size();
    }

    public Puntuacion buscarPuntuancionPorCinemaniaco(Cinemaniaco cinemaniaco) {
        for (Puntuacion p : puntuaciones) {
            if (p.getCinemaniaco().equals(cinemaniaco)) {
                return p;
            }
        }
        return null; // No se encontró una puntuación para ese cinemaniaco
    }

    public void modificarPuntuacion(Cinemaniaco cinemaniaco, double puntuacion2) {
        Puntuacion puntuacion = buscarPuntuancionPorCinemaniaco(cinemaniaco);
        if(puntuacion != null){
            puntuacion.setPuntuacion(puntuacion2);
        }
    }

    public void anadirComentario(Comentario comentario) {
        comentarios.add(comentario);
    }

    public Comentario buscarComentarioDe(Cinemaniaco cinemaniaco) {
        return this.comentarios.stream()
                .filter(comentario -> comentario.getCinemaniaco().equals(cinemaniaco))
                .findFirst()
                .orElse(null);
    }
}
