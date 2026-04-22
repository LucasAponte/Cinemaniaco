package com.cinemaniaco.Cinemanico.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int edad;
    private String apellido;
    @Column(name="email" ,unique = true, nullable = false)
    private String email;

    public Persona(String nombre, int edad, String apellido) {
        this.nombre = nombre;
        this.edad = edad;
        this.apellido = apellido;
    }

    public Persona(int edad, String nombre, String apellido, String email) {
        this.edad = edad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }
}
