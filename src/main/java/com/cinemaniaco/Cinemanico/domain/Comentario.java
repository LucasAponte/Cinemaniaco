package com.cinemaniaco.Cinemanico.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Comentario {
    @Id
    private Long id_Comentario;
    @ManyToOne
    @JoinColumn(name = "cinemaniaco_id")
    private Cinemaniaco cinemaniaco;
    private int meGusta;
    private String texto;
    @OneToMany
    private List<Comentario> comentarios;
    
}
