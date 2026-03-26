package com.cinemaniaco.Cinemanico.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PuntuacionTest {
    Persona persona = new Persona("Leo",5,"Lopez");
    Pelicula pelicula = new Pelicula("Inception", "Christopher Nolan", 2010, List.of("Leo"),List.of("drama") );
    Cinemaniaco cinemaniaco = new Cinemaniaco( persona,"leito");

    @Test
    @DisplayName("Crear puntuación con cinemaniaco y película devuelve valor correcto")
    public void crearPuntuacionConCinemaniacoYPeliculaDevuelveValorCorrecto(){
        double puntuacion = 4.5;
        pelicula.anadirPuntuacion(cinemaniaco,puntuacion);
        assertEquals(1,pelicula.cantPuntuaciones());
        assertEquals(puntuacion, pelicula.getPuntuaciones().get(0).getPuntuacion());
        assertEquals(cinemaniaco, pelicula.getPuntuaciones().get(0).getCinemaniaco());
    }
    @Test
    @DisplayName("Evitar puntuación negativa devuelve valor correcto")
    public void evitarPuntuacionNegativaDevuelveValorCorrecto(){
        double puntuacionNegativa = -3.0;
        pelicula.anadirPuntuacion(cinemaniaco,puntuacionNegativa);
        assertEquals(1,pelicula.cantPuntuaciones());
        assertEquals(puntuacionNegativa, pelicula.getPuntuaciones().get(0).getPuntuacion());
    }

    @Test
    @DisplayName("Evitar puntuaciones dobles del mismo cinemaniaco devuelve valor correcto")
    //Si bien marco que no se pueda subir doble puntuacion, si debería poder dejar modificar una puntuacion ya existente...
    //puestoe es distinto subir otra puntuacion que modificar otra
    public void evitarPuntuacionesDoblesMismoCinemaniacoDevuelveValorCorrecto(){
        double puntuacion1 = 4.5;
        double puntuacion2 = 3.0;
        pelicula.anadirPuntuacion(cinemaniaco,puntuacion1);
        pelicula.anadirPuntuacion(cinemaniaco,puntuacion2);
        assertEquals(1,pelicula.cantPuntuaciones());
        assertFalse(puntuacion1 == pelicula.buscarPuntuacionPorCinemaniaco(cinemaniaco).getPuntuacion());
        assertTrue(puntuacion2 == pelicula.buscarPuntuacionPorCinemaniaco(cinemaniaco).getPuntuacion());
    }

    @Test
    @DisplayName("Modificar puntuación existente devuelve valor correcto")
    public void modificarPuntuacionExistenteDevuelveValorCorrecto(){
        double puntuacion1 = 4.5;
        double puntuacion2 = 3.0;
        pelicula.anadirPuntuacion(cinemaniaco,puntuacion1);
        pelicula.anadirPuntuacion(cinemaniaco,puntuacion2);
        assertEquals(1,pelicula.cantPuntuaciones());
        assertEquals(puntuacion2, pelicula.buscarPuntuacionPorCinemaniaco(cinemaniaco).getPuntuacion());
    }

    @Test
    @DisplayName("Muchas puntuaciones y calcular promedio devuelve valor correcto")
    public void muchasPuntuacionesYCalcularPromedioDevuelveValorCorrecto(){
        Cinemaniaco cinemaniaco2 = new Cinemaniaco( new Persona("Leo",5,"Lopez"),"leito2");
        Cinemaniaco cinemaniaco3 = new Cinemaniaco( new Persona("Leo",5,"Lopez"),"leito3");
        double puntuacion1 = 4.5;
        double puntuacion2 = 3.5;
        double puntuacion3 = 2.5;
        pelicula.anadirPuntuacion(cinemaniaco,puntuacion1);
        pelicula.anadirPuntuacion(cinemaniaco2,puntuacion2);
        pelicula.anadirPuntuacion(cinemaniaco3,puntuacion3);
        assertEquals(3,pelicula.cantPuntuaciones());
        //Esto es para comparar nomas, el calcular promedio lo hace pelicula.
        double promedio = (puntuacion1 + puntuacion2 + puntuacion3) / pelicula.cantPuntuaciones();
        assertEquals(promedio, pelicula.calcularPuntuacionPromedio());
    }
}
