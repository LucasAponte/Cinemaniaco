package com.cinemaniaco.Cinemanico.dto.response;

import com.cinemaniaco.Cinemanico.domain.Persona;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private int edad;
    private String email;

    public static PersonaResponse from(Persona persona) {
        if (persona == null) return null;
        return new PersonaResponse(
                persona.getId_Persona(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getEdad(),
                persona.getEmail()
        );
    }
}
