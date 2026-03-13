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

import java.util.ArrayList;
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
    private int meGusta = 0;
    private List<Cinemaniaco> meGustaCinemaniacos = new ArrayList<>();
    private String texto;
    @OneToMany
    private List<Comentario> subComentarios = new ArrayList<>();

    public Comentario(Cinemaniaco cinemaniaco, String texto) {
        this.cinemaniaco = cinemaniaco;
        this.texto = texto;
    }
    public void agregarComentario(Comentario comentario) {
        this.subComentarios.add(comentario);
    }
    public Comentario buscarSubComentarioDe(Cinemaniaco cinemaniaco){
        return this.subComentarios.stream().filter(comentario -> comentario.getCinemaniaco().equals(cinemaniaco)).findFirst().orElse(null);
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

    public void quitarMegustaDe(Cinemaniaco cinemaniaco) {
        if(!tieneMeGustaDe(cinemaniaco)) {
            this.meGustaCinemaniacos.remove(cinemaniaco);
            this.meGusta--;
        }
    }
}
