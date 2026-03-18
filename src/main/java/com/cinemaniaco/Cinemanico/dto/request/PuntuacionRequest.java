package com.cinemaniaco.Cinemanico.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PuntuacionRequest {

    @NotNull(message = "El id del cinemaniaco es obligatorio")
    private Long cinemaniacoId;

    @DecimalMin(value = "0.0", message = "La puntuación mínima es 0")
    @DecimalMax(value = "10.0", message = "La puntuación máxima es 10")
    private double puntos;
}
