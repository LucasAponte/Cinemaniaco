package com.cinemaniaco.Cinemanico.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class PersonaTest {
    @Test
    @DisplayName("Creación de Persona devuelve valor correcto")
    public void creacionDePersonaDevuelveValorCorrecto() {
        // Crear una instancia de Persona
        Persona persona = new Persona("Juan", "Pérez",30 );

        // Verificar que los atributos se asignaron correctamente
        assert persona.getNombre().equals("Juan");
        assert persona.getEdad() == 30;
        assert persona.getApellido().equals("Pérez");

    }

}
