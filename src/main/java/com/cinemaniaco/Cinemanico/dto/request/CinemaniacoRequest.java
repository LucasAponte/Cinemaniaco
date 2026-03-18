package com.cinemaniaco.Cinemanico.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaniacoRequest {

    @Valid
    @NotNull(message = "Los datos de la persona son obligatorios")
    private PersonaRequest persona;

    @NotBlank(message = "El apodo es obligatorio")
    private String apodo;
}
