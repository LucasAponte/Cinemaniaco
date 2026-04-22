package com.cinemaniaco.Cinemanico.dto.response;

import com.cinemaniaco.Cinemanico.domain.Puntuacion;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PuntuacionResponse {

    private Long id;
    private double puntuacion;
    private String apodoCinemaniaco;

    public static PuntuacionResponse from(Puntuacion p) {
        if (p == null) return null;
        return new PuntuacionResponse(
                p.getId(),
                p.getPuntuacion(),
                p.getCinemaniaco() != null ? p.getCinemaniaco().getApodo() : null
        );
    }
}
