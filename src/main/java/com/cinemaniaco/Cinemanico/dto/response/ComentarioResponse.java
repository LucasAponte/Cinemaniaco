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
        return from(c, 0);
    }

    private static ComentarioResponse from(Comentario c, int depth) {
        if (c == null) return null;
        int maxDepth = 2; // Limitar la profundidad de los subcomentarios
        return new ComentarioResponse(
                c.getId(),
                c.getTexto(),
                c.getMeGusta(),
                c.getCinemaniaco() != null ? c.getCinemaniaco().getApodo() : null,
                c.cantidadSubComentarios(),
                (depth < maxDepth && c.getSubComentarios() != null)
                        ? c.getSubComentarios().stream().map(sub -> from(sub, depth + 1)).toList()
                        : List.of()
        );
    }
}
