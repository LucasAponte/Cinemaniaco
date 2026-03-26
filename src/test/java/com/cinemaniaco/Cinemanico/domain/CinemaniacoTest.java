package com.cinemaniaco.Cinemanico.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class CinemaniacoTest {

    @Test
    @DisplayName("Cinemaniatico no puede seguirse a sí mismo, devuelve False")
    public void seguirNoSePuedeSeguirASiMismoDevuelveFalse() {
        Persona p = new Persona();
        Cinemaniaco c = new Cinemaniaco(p, "uno");
        c.setId_Cinemaniaco(1L);
        assertFalse(c.seguir(c));
    }

    @Test
    @DisplayName("Seguir agrega seguidor y seguido, devuelve valor correcto")
    public void seguirAgregaSeguidorYSeguidoDevuelveTrue() {
        Persona p1 = new Persona();
        Persona p2 = new Persona();
        Cinemaniaco c1 = new Cinemaniaco(p1, "uno");
        Cinemaniaco c2 = new Cinemaniaco(p2, "dos");
        c1.setId_Cinemaniaco(1L);
        c2.setId_Cinemaniaco(2L);

        assertTrue(c1.seguir(c2));
        assertTrue(c1.getSeguidos().contains(c2));
        assertTrue(c2.getSeguidores().contains(c1));
    }

    @Test
    @DisplayName("Contar seguidores y es seguidor devuelve valor correcto")
    public void contarSeguidoresYEsSeguidorDevuelveValorCorrecto() {
        Persona p1 = new Persona();
        Persona p2 = new Persona();
        Persona p3 = new Persona();
        Cinemaniaco c1 = new Cinemaniaco(p1, "uno");
        Cinemaniaco c2 = new Cinemaniaco(p2, "dos");
        Cinemaniaco c3 = new Cinemaniaco(p3, "tres");
        c1.setId_Cinemaniaco(1L);
        c2.setId_Cinemaniaco(2L);
        c3.setId_Cinemaniaco(3L);

        c2.seguir(c1);
        c3.seguir(c1);

        assertEquals(2, c1.contarSeguidores());
        assertTrue(c1.esSeguidor(c2)); // c1 no es seguidor de c2
        assertFalse(c1.esSeguidor(null));
        assertTrue(c1.esSeguidor(c3));
        assertFalse(c2.esSeguidor(c1));
        assertFalse(c3.esSeguidor(c1));
    }

    @Test
    @DisplayName("Amigos en común funciona devuelve valor correcto")
    public void amigosEnComunFuncionaDevuelveValorCorrecto() {
        Persona p1 = new Persona();
        Persona p2 = new Persona();
        Persona p3 = new Persona();
        Persona p4 = new Persona();
        Cinemaniaco a = new Cinemaniaco(p1, "A");
        Cinemaniaco b = new Cinemaniaco(p2, "B");
        Cinemaniaco c = new Cinemaniaco(p3, "C");
        Cinemaniaco d = new Cinemaniaco(p4, "D");
        a.setId_Cinemaniaco(1L);
        b.setId_Cinemaniaco(2L);
        c.setId_Cinemaniaco(3L);
        d.setId_Cinemaniaco(4L);

        // A sigue a C y D
        a.seguir(c);
        a.seguir(d);
        // B sigue a D
        b.seguir(d);

        // amigos en común entre A y B -> D
        assertEquals(1, a.amigosEnComun(b).size());
        assertTrue(a.amigosEnComun(b).contains(d));
    }
}
