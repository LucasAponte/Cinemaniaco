package com.cinemaniaco.Cinemanico.controller;

import com.cinemaniaco.Cinemanico.dto.request.CinemaniacoRequest;
import com.cinemaniaco.Cinemanico.dto.response.CinemaniacoResponse;
import com.cinemaniaco.Cinemanico.service.CinemaniacoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemaniacos")
@RequiredArgsConstructor
public class CinemaniacoController {

    private final CinemaniacoService cinemaniacoService;

    // ─── CRUD ───────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<CinemaniacoResponse>> listarTodos() {
        return ResponseEntity.ok(cinemaniacoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaniacoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.buscarPorIdDto(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<CinemaniacoResponse> buscarPorApodo(@RequestParam String apodo) {
        return ResponseEntity.ok(cinemaniacoService.buscarPorApodoDto(apodo));
    }

    @PostMapping
    public ResponseEntity<CinemaniacoResponse> registrar(@Valid @RequestBody CinemaniacoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cinemaniacoService.registrar(request));
    }

    @PatchMapping("/{id}/apodo")
    public ResponseEntity<CinemaniacoResponse> actualizarApodo(
            @PathVariable Long id,
            @RequestParam String nuevoApodo) {
        return ResponseEntity.ok(cinemaniacoService.actualizarApodo(id, nuevoApodo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cinemaniacoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Social ─────────────────────────────────────────────────────────────

    @PostMapping("/{id}/seguir/{idASeguir}")
    public ResponseEntity<String> seguir(
            @PathVariable Long id,
            @PathVariable Long idASeguir) {
        boolean resultado = cinemaniacoService.seguir(id, idASeguir);
        if (!resultado) return ResponseEntity.badRequest().body("Ya seguís a este cinemaniaco");
        return ResponseEntity.ok("Ahora seguís al cinemaniaco con id: " + idASeguir);
    }

    @DeleteMapping("/{id}/seguir/{idASeguir}")
    public ResponseEntity<String> dejarDeSeguir(
            @PathVariable Long id,
            @PathVariable Long idASeguir) {
        boolean resultado = cinemaniacoService.dejarDeSeguir(id, idASeguir);
        if (!resultado) return ResponseEntity.badRequest().body("No seguís a este cinemaniaco");
        return ResponseEntity.ok("Dejaste de seguir al cinemaniaco con id: " + idASeguir);
    }

    @GetMapping("/{id}/seguidos")
    public ResponseEntity<List<CinemaniacoResponse>> obtenerSeguidos(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.obtenerSeguidos(id));
    }

    @GetMapping("/{id}/seguidores")
    public ResponseEntity<List<CinemaniacoResponse>> obtenerSeguidores(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.obtenerSeguidores(id));
    }

    @GetMapping("/{id}/seguidores/cantidad")
    public ResponseEntity<Integer> contarSeguidores(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.contarSeguidores(id));
    }

    @GetMapping("/{idA}/amigos-en-comun/{idB}")
    public ResponseEntity<List<CinemaniacoResponse>> amigosEnComun(
            @PathVariable Long idA,
            @PathVariable Long idB) {
        return ResponseEntity.ok(cinemaniacoService.amigosEnComun(idA, idB));
    }
}