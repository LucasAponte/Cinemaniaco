package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Cinemaniaco {
    @Id
    private Long id_Cinemaniaco;
    @OneToOne
    private Persona persona;
    private String Apodo;
    @OneToMany
    private List<Pelicula> peliculas;
    @ManyToMany
    private List<Cinemaniaco> amigos;

    public Cinemaniaco(Persona persona, String apodo) {
        this.persona = persona;
        Apodo = apodo;
    }
}
