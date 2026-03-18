package com.cinemaniaco.Cinemanico.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Max(value = 120, message = "La edad no puede superar 120")
    private int edad;

    @Email(message = "El email no tiene formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
