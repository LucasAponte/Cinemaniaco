package com.cinemaniaco.Cinemanico.dto.response;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaniacoResponse {

    private Long id;
    private String apodo;
    private PersonaResponse persona;
    private int cantSeguidores;
    private int cantSeguidos;

    public static CinemaniacoResponse from(Cinemaniaco c) {
        if (c == null) return null;
        return new CinemaniacoResponse(
                c.getId_Cinemaniaco(),
                c.getApodo(),
                PersonaResponse.from(c.getPersona()),
                c.contarSeguidores(),
                c.getSeguidos() != null ? c.getSeguidos().size() : 0
        );
    }
}
