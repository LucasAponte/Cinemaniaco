package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Comparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "comparacion_cinemaniacos",
            joinColumns = @JoinColumn(name = "comparacion_id"),
            inverseJoinColumns = @JoinColumn(name = "cinemaniaco_id")
    )
    private List<Cinemaniaco> cinemaniacos;

    @ManyToMany
    @JoinTable(
            name = "comparacion_peliculas",
            joinColumns = @JoinColumn(name = "comparacion_id"),
            inverseJoinColumns = @JoinColumn(name = "pelicula_id")
    )
    private List<Pelicula> peliculas;
}