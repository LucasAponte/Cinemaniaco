package com.cinemaniaco.Cinemanico.repository;

import com.cinemaniaco.Cinemanico.domain.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
}

