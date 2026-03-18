package com.cinemaniaco.Cinemanico.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioRequest {

    @NotNull(message = "El id del cinemaniaco es obligatorio")
    private Long cinemaniacoId;

    @NotBlank(message = "El texto no puede estar vacío")
    private String texto;
}
