package com.cinemaniaco.Cinemanico.repository;

import com.cinemaniaco.Cinemanico.domain.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}

