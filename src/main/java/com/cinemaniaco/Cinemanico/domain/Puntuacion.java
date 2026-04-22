package com.cinemaniaco.Cinemanico.domain;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Puntuacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "cinemaniaco_id")
    private Cinemaniaco cinemaniaco;
    private double puntuacion;

    public Puntuacion(Cinemaniaco cinemaniaco, double puntuacion) {
        this.cinemaniaco = cinemaniaco;
        this.puntuacion = puntuacion;
    }

}
