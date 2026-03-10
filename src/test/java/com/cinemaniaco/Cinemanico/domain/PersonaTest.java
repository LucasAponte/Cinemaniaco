package com.cinemaniaco.Cinemanico.domain;

import org.junit.jupiter.api.Test;

public class PersonaTest {
    @Test
    public void testPersonaCreation() {
        // Crear una instancia de Persona
        Persona persona = new Persona("Juan", 30, "Pérez");

        // Verificar que los atributos se asignaron correctamente
        assert persona.getNombre().equals("Juan");
        assert persona.getEdad() == 30;
        assert persona.getApellido().equals("Pérez");

    }

}
