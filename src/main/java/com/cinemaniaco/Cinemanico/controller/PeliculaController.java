package com.cinemaniaco.Cinemanico.controller;

import com.cinemaniaco.Cinemanico.dto.request.ComentarioRequest;
import com.cinemaniaco.Cinemanico.dto.request.PeliculaRequest;
import com.cinemaniaco.Cinemanico.dto.request.PuntuacionRequest;
import com.cinemaniaco.Cinemanico.dto.response.ComentarioResponse;
import com.cinemaniaco.Cinemanico.dto.response.PeliculaResponse;
import com.cinemaniaco.Cinemanico.dto.response.PuntuacionResponse;
import com.cinemaniaco.Cinemanico.service.PeliculaService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<PeliculaResponse>> listarTodas() {
        return ResponseEntity.ok(peliculaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.buscarPorIdDto(id));
    }

    @PostMapping
    public ResponseEntity<PeliculaResponse> crear(@Valid @RequestBody PeliculaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeliculaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PeliculaRequest request) {
        return ResponseEntity.ok(peliculaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Puntuaciones ───────────────────────────────────────────────────────

    @PostMapping("/{id}/puntuaciones")
    public ResponseEntity<Double> puntuar(
            @PathVariable Long id,
            @Valid @RequestBody PuntuacionRequest request) {
        return ResponseEntity.ok(peliculaService.puntuarPelicula(id, request));
    }

    @GetMapping("/{id}/puntuaciones/promedio")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.obtenerPromedio(id));
    }

    @GetMapping("/{id}/puntuaciones/cinemaniaco/{cinemaniacoId}")
    public ResponseEntity<PuntuacionResponse> obtenerPuntuacionDe(
            @PathVariable Long id,
            @PathVariable Long cinemaniacoId) {
        return ResponseEntity.ok(peliculaService.obtenerPuntuacionDe(id, cinemaniacoId));
    }

    // ─── Comentarios ────────────────────────────────────────────────────────

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioResponse> comentar(
            @PathVariable Long id,
            @Valid @RequestBody ComentarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.comentarPelicula(id, request));
    }

    @PostMapping("/comentarios/{comentarioId}/respuestas")
    public ResponseEntity<ComentarioResponse> responder(
            @PathVariable Long comentarioId,
            @Valid @RequestBody ComentarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peliculaService.responderComentario(comentarioId, request));
    }

    // ─── Me Gusta ───────────────────────────────────────────────────────────

    @PostMapping("/comentarios/{comentarioId}/megusta")
    public ResponseEntity<Integer> darMeGusta(
            @PathVariable Long comentarioId,
            @RequestParam Long cinemaniacoId) {
        return ResponseEntity.ok(peliculaService.darMeGusta(comentarioId, cinemaniacoId));
    }

    @DeleteMapping("/comentarios/{comentarioId}/megusta")
    public ResponseEntity<Integer> quitarMeGusta(
            @PathVariable Long comentarioId,
            @RequestParam Long cinemaniacoId) {
        return ResponseEntity.ok(peliculaService.quitarMeGusta(comentarioId, cinemaniacoId));
    }

        // ─── Resumen IA ─────────────────────────────────────────────────────────

    /**
     * POST /api/peliculas/{id}/resumen-ia
     * Genera un resumen con IA a partir de todos los comentarios de la película y lo persiste.
     */
    //TODO no debería realizar un Comentario resumen cunado alguien lo pida si no que debería ser un atributo y se actualice cada tanto.
    //TODO hacer otra entidad que sea OpenIAClientee que se encargue de eso y no mezclarlo con el servicio de Pelicula.
    @PostMapping("/{id}/resumen-ia")
    public ResponseEntity<String> generarResumenIA(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.generarComentarioEnComunIA(id));
    }
    /**
     * GET /api/peliculas/{id}/resumen-ia
     * Devuelve el último resumen generado (sin volver a llamar a OpenAI).
     */
    @GetMapping("/{id}/resumen-ia")
    public ResponseEntity<String> obtenerResumenIA(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.obtenerComentarioEnComunIa(id));
    }
}