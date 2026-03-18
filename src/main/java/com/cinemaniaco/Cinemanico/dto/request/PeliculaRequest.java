package com.cinemaniaco.Cinemanico.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El director es obligatorio")
    private String director;

    @Min(value = 1888, message = "El año de estreno no es válido")
    private int anioEstreno;

    @NotEmpty(message = "Debe incluir al menos un actor")
    private List<String> actores;

    @NotEmpty(message = "Debe incluir al menos un género")
    private List<String> generos;
}
