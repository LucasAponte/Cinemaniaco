package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Comparacion {
    @Id
    private Long id_Comparacion;
    private List<Cinemaniaco> cinemaniacos;
    private List<Pelicula> peliculas;

}
