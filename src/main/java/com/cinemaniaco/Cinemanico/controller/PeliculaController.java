package com.cinemaniaco.Cinemanico.controller;

import com.cinemaniaco.Cinemanico.domain.Comentario;
import com.cinemaniaco.Cinemanico.domain.Pelicula;
import com.cinemaniaco.Cinemanico.domain.Puntuacion;
import com.cinemaniaco.Cinemanico.service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final PeliculaService peliculaService;

    // ─── CRUD ───────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Pelicula>> listarTodas() {
        return ResponseEntity.ok(peliculaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Pelicula> crear(@RequestBody Pelicula pelicula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.crear(pelicula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizar(@PathVariable Long id, @RequestBody Pelicula datos) {
        return ResponseEntity.ok(peliculaService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Puntuaciones ───────────────────────────────────────────────────────

    /**
     * POST /api/peliculas/{id}/puntuaciones?cinemaniacoId=1&puntos=8.5
     */
    @PostMapping("/{id}/puntuaciones")
    public ResponseEntity<Double> puntuar(
            @PathVariable Long id,
            @RequestParam Long cinemaniacoId,
            @RequestParam double puntos) {
        double promedio = peliculaService.puntuarPelicula(id, cinemaniacoId, puntos);
        return ResponseEntity.ok(promedio);
    }

    @GetMapping("/{id}/puntuaciones/promedio")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.obtenerPromedio(id));
    }

    @GetMapping("/{id}/puntuaciones/cinemaniaco/{cinemaniacoId}")
    public ResponseEntity<Puntuacion> obtenerPuntuacionDe(
            @PathVariable Long id,
            @PathVariable Long cinemaniacoId) {
        Puntuacion puntuacion = peliculaService.obtenerPuntuacionDe(id, cinemaniacoId);
        if (puntuacion == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(puntuacion);
    }

    // ─── Comentarios ────────────────────────────────────────────────────────

    /**
     * POST /api/peliculas/{id}/comentarios?cinemaniacoId=1
     * Body: { "texto": "..." }
     */
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<Comentario> comentar(
            @PathVariable Long id,
            @RequestParam Long cinemaniacoId,
            @RequestBody String texto) {
        Comentario comentario = peliculaService.comentarPelicula(id, cinemaniacoId, texto);
        return ResponseEntity.status(HttpStatus.CREATED).body(comentario);
    }

    /**
     * POST /api/peliculas/comentarios/{comentarioId}/respuestas?cinemaniacoId=1
     * Body: { "texto": "..." }
     */
    @PostMapping("/comentarios/{comentarioId}/respuestas")
    public ResponseEntity<Comentario> responder(
            @PathVariable Long comentarioId,
            @RequestParam Long cinemaniacoId,
            @RequestBody String texto) {
        Comentario respuesta = peliculaService.responderComentario(comentarioId, cinemaniacoId, texto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // ─── Me Gusta ───────────────────────────────────────────────────────────

    @PostMapping("/comentarios/{comentarioId}/megusta")
    public ResponseEntity<Integer> darMeGusta(
            @PathVariable Long comentarioId,
            @RequestParam Long cinemaniacoId) {
        int total = peliculaService.darMeGusta(comentarioId, cinemaniacoId);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/comentarios/{comentarioId}/megusta")
    public ResponseEntity<Integer> quitarMeGusta(
            @PathVariable Long comentarioId,
            @RequestParam Long cinemaniacoId) {
        int total = peliculaService.quitarMeGusta(comentarioId, cinemaniacoId);
        return ResponseEntity.ok(total);
    }

    // ─── Resumen IA ─────────────────────────────────────────────────────────

    /**
     * POST /api/peliculas/{id}/resumen-ia
     * Genera un resumen con IA a partir de todos los comentarios de la película y lo persiste.
     */
    @PostMapping("/{id}/resumen-ia")
    public ResponseEntity<String> generarResumenIA(@PathVariable Long id) {
        String resumen = peliculaService.generarResumenIA(id);
        return ResponseEntity.ok(resumen);
    }

    /**
     * GET /api/peliculas/{id}/resumen-ia
     * Devuelve el último resumen generado (sin volver a llamar a OpenAI).
     */
    @GetMapping("/{id}/resumen-ia")
    public ResponseEntity<String> obtenerResumenIA(@PathVariable Long id) {
        String resumen = peliculaService.obtenerResumenIA(id);
        return ResponseEntity.ok(resumen);
    }
}