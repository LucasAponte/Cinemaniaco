package com.cinemaniaco.Cinemanico.controller;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Persona;
import com.cinemaniaco.Cinemanico.service.CinemaniacoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cinemaniacos")
@RequiredArgsConstructor
public class CinemaniacoController {

    private final CinemaniacoService cinemaniacoService;

    // ─── CRUD ───────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Cinemaniaco>> listarTodos() {
        return ResponseEntity.ok(cinemaniacoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cinemaniaco> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Cinemaniaco> buscarPorApodo(@RequestParam String apodo) {
        return ResponseEntity.ok(cinemaniacoService.buscarPorApodo(apodo));
    }

    /**
     * POST /api/cinemaniacos
     * Body: { "persona": { "nombre": "...", "apellido": "...", "edad": 25, "email": "..." }, "apodo": "..." }
     */
    @PostMapping
    public ResponseEntity<Cinemaniaco> registrar(@RequestBody Map<String, Object> body) {
        // Se reciben los datos anidados para crear Persona + Cinemaniaco en un solo request
        @SuppressWarnings("unchecked")
        Map<String, Object> personaData = (Map<String, Object>) body.get("persona");

        Persona persona = new Persona(
                (Integer) personaData.get("edad"),
                (String) personaData.get("nombre"),
                (String) personaData.get("apellido"),
                (String) personaData.get("email")
        );
        String apodo = (String) body.get("apodo");

        Cinemaniaco nuevo = cinemaniacoService.registrar(persona, apodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PatchMapping("/{id}/apodo")
    public ResponseEntity<Cinemaniaco> actualizarApodo(
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

    /**
     * POST /api/cinemaniacos/{id}/seguir/{idASeguir}
     */
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
    public ResponseEntity<List<Cinemaniaco>> obtenerSeguidos(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.obtenerSeguidos(id));
    }

    @GetMapping("/{id}/seguidores")
    public ResponseEntity<List<Cinemaniaco>> obtenerSeguidores(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.obtenerSeguidores(id));
    }

    @GetMapping("/{id}/seguidores/cantidad")
    public ResponseEntity<Integer> contarSeguidores(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaniacoService.contarSeguidores(id));
    }

    @GetMapping("/{idA}/amigos-en-comun/{idB}")
    public ResponseEntity<List<Cinemaniaco>> amigosEnComun(
            @PathVariable Long idA,
            @PathVariable Long idB) {
        return ResponseEntity.ok(cinemaniacoService.amigosEnComun(idA, idB));
    }
}