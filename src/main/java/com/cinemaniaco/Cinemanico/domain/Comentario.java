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
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cinemaniaco_id")
    private Cinemaniaco cinemaniaco;

    private int meGusta = 0;

    @ManyToMany
    @JoinTable(
            name = "comentario_me_gusta",
            joinColumns = @JoinColumn(name = "comentario_id"),
            inverseJoinColumns = @JoinColumn(name = "cinemaniaco_id")
    )
    private List<Cinemaniaco> meGustaCinemaniacos = new ArrayList<>();

    private String texto;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comentario_padre_id")
    private List<Comentario> subComentarios = new ArrayList<>();

    public Comentario(Cinemaniaco cinemaniaco, String texto) {
        this.cinemaniaco = cinemaniaco;
        this.texto = texto;
    }

    public void agregarComentario(Comentario comentario) {
        this.subComentarios.add(comentario);
    }

    public Comentario buscarSubComentarioDe(Cinemaniaco cinemaniaco) {
        return this.subComentarios.stream()
                .filter(c -> c.getCinemaniaco().equals(cinemaniaco))
                .findFirst()
                .orElse(null);
    }

    public int cantidadSubComentarios() {
        return subComentarios.size();
    }

    public void agregarMeGusta(Cinemaniaco cinemaniaco) {
        if (!this.meGustaCinemaniacos.contains(cinemaniaco)) {
            this.meGustaCinemaniacos.add(cinemaniaco);
            this.meGusta++;
        }
    }

    public boolean tieneMeGustaDe(Cinemaniaco cinemaniaco) {
        return this.meGustaCinemaniacos.contains(cinemaniaco);
    }

    public void quitarMeGustaDe(Cinemaniaco cinemaniaco) {
        if (tieneMeGustaDe(cinemaniaco)) {
            this.meGustaCinemaniacos.remove(cinemaniaco);
            this.meGusta--;
        }
    }
}