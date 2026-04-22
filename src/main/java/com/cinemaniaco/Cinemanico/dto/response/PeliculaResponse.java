package com.cinemaniaco.Cinemanico.dto.response;

import com.cinemaniaco.Cinemanico.domain.Pelicula;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaResponse {

    private Long id;
    private String titulo;
    private String director;
    private int anioEstreno;
    private List<String> actores;
    private List<String> generos;
    private double puntuacionPromedio;
    private int cantPuntuaciones;
    private List<ComentarioResponse> comentarios;
    private String resumenIA;

    public static PeliculaResponse from(Pelicula p) {
        if (p == null) return null;
        return new PeliculaResponse(
                p.getId(),
                p.getTitulo(),
                p.getDirector(),
                p.getAnioEstreno(),
                p.getActores(),
                p.getGeneros(),
                p.calcularPuntuacionPromedio(),
                p.cantPuntuaciones(),
                p.getComentarios() != null
                        ? p.getComentarios().stream().map(ComentarioResponse::from).toList()
                        : List.of(),
                p.getComentarioEnComunIA()
        );
    }
}
