package com.cinemaniaco.Cinemanico.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ComentarioTest {
    Persona persona = new Persona("Juan", 30, "Pérez");
    Cinemaniaco cinemaniaco = new Cinemaniaco(persona, "CineLover");
    Pelicula pelicula = new Pelicula("Inception", "Christopher Nolan", 2010, List.of("Leo"),List.of("drama") );


    @Test
    public void testCreacionComentarioEnPelicula() {
        // Crear una instancia de Comentario
        Comentario comentario = new Comentario(cinemaniaco, "¡Me encantó esta película!");
        // Verificar que los atributos se asignaron correctamente
        pelicula.anadirComentario(comentario);
        assert comentario.getCinemaniaco().equals(cinemaniaco);
        assert comentario.getTexto().equals("¡Me encantó esta película!");
        assert pelicula.getComentarios().contains(comentario);
        assert pelicula.buscarComentarioDe(cinemaniaco).equals(comentario);
    }
    @Test
    public void testPonerComentariosEnUnComentario() {
        Cinemaniaco cinemaniaco2 = new Cinemaniaco( new Persona("Leo",5,"Lopez"),"leito2");
        Cinemaniaco cinemaniaco3 = new Cinemaniaco( new Persona("Leo",5,"Lopez"),"leito3");
        Comentario comentarioPrincipal = new Comentario(cinemaniaco, "¡Me encantó esta película!");
        Comentario comentarioSecundario = new Comentario(cinemaniaco2, "Le doy un 13/10");
        Comentario comentarioSecundario2 = new Comentario(cinemaniaco3, "Totalmente de acuerdo!");
        comentarioPrincipal.agregarComentario(comentarioSecundario);
        comentarioPrincipal.agregarComentario(comentarioSecundario2);
        assert comentarioPrincipal.buscarSubComentarioDe(cinemaniaco2).equals(comentarioSecundario);
        assert comentarioPrincipal.buscarSubComentarioDe(cinemaniaco3).equals(comentarioSecundario2);
        assert comentarioPrincipal.cantidadSubComentarios() == 2;
    }
    @Test
    public void agregarMeGustaAComentario() {
        Cinemaniaco cinemaniaco2 = new Cinemaniaco( new Persona("Leo",5,"Lopez"),"leito2");
        Comentario comentario = new Comentario(cinemaniaco, "¡Me encantó esta película!");
        comentario.agregarMeGusta(cinemaniaco);
        assert comentario.getMeGusta() == 1;
        assert comentario.tieneMeGustaDe(cinemaniaco);
        // Verificar que no se pueda agregar un "Me gusta" duplicado
        comentario.agregarMeGusta(cinemaniaco);
        assert comentario.getMeGusta() == 1; // No debería aumentar
        comentario.agregarMeGusta(cinemaniaco2);
        assert comentario.getMeGusta() == 2;
        assert comentario.tieneMeGustaDe(cinemaniaco2);
    }
    @Test
    public void quitarMeGustaDeComentario() {
        Cinemaniaco cinemaniaco2 = new Cinemaniaco( new Persona("Leo",5,"Lopez"),"leito2");
        Comentario comentario = new Comentario(cinemaniaco, "¡Me encantó esta película!");
        comentario.agregarMeGusta(cinemaniaco);
        comentario.agregarMeGusta(cinemaniaco2);
        assert comentario.getMeGusta() == 2;
        // Simular quitar un "Me gusta"
        comentario.quitarMeGustaDe(cinemaniaco);
        comentario.setMeGusta(comentario.getMeGusta() - 1);
        assert comentario.getMeGusta() == 1;
        assert !comentario.tieneMeGustaDe(cinemaniaco);
    }
}
