package com.cinemaniaco.Cinemanico.domain;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Long id_Puntuacion;
    @ManyToOne(targetEntity = Cinemaniaco.class, optional = false)
    @JoinColumn(name = "cinemaniaco_id")
    private Cinemaniaco cinemaniaco;
    private double puntuacion;

}
