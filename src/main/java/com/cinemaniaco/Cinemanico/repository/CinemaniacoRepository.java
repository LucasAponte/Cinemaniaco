package com.cinemaniaco.Cinemanico.repository;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CinemaniacoRepository extends JpaRepository<Cinemaniaco, Long> {
    Optional<Cinemaniaco> findByApodo(String apodo);
}

