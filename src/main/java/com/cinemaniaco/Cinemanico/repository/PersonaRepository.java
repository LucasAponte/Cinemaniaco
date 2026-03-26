package com.cinemaniaco.Cinemanico.repository;

import com.cinemaniaco.Cinemanico.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}

