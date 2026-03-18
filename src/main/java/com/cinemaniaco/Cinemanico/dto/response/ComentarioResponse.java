package com.cinemaniaco.Cinemanico.dto.response;

import com.cinemaniaco.Cinemanico.domain.Comentario;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioResponse {

    private Long id;
    private String texto;
    private int meGusta;
    private String apodoCinemaniaco;
    private int cantSubComentarios;
    private List<ComentarioResponse> subComentarios;

    public static ComentarioResponse from(Comentario c) {
        if (c == null) return null;
        return new ComentarioResponse(
                c.getId_Comentario(),
                c.getTexto(),
                c.getMeGusta(),
                //No debería poder ser Null, pero por las dudas lo manejo
                c.getCinemaniaco() != null ? c.getCinemaniaco().getApodo() : null,
                c.cantidadSubComentarios(),
                //TODO: Esto puede generar un ciclo infinito si hay subcomentarios que a su vez tienen subcomentarios?
                c.getSubComentarios() != null
                        ? c.getSubComentarios().stream().map(ComentarioResponse::from).toList()
                        : List.of()
        );
    }
}
