package com.cinemaniaco.Cinemanico.service;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Persona;
import com.cinemaniaco.Cinemanico.repository.CinemaniacoRepository;
import com.cinemaniaco.Cinemanico.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaniacoService {

    private final CinemaniacoRepository cinemaniacoRepository;
    private final PersonaRepository personaRepository;

    // ─── CRUD ───────────────────────────────────────────────────────────────

    public List<Cinemaniaco> listarTodos() {
        return cinemaniacoRepository.findAll();
    }

    public Cinemaniaco buscarPorId(Long id) {
        return cinemaniacoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado con id: " + id));
    }

    public Cinemaniaco buscarPorApodo(String apodo) {
        return cinemaniacoRepository.findByApodo(apodo)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado con apodo: " + apodo));
    }

    @Transactional
    public Cinemaniaco registrar(Persona persona, String apodo) {
        Persona personaGuardada = personaRepository.save(persona);
        Cinemaniaco cinemaniaco = new Cinemaniaco(personaGuardada, apodo);
        return cinemaniacoRepository.save(cinemaniaco);
    }

    @Transactional
    public Cinemaniaco actualizarApodo(Long id, String nuevoApodo) {
        Cinemaniaco cinemaniaco = buscarPorId(id);
        cinemaniaco.setApodo(nuevoApodo);
        return cinemaniacoRepository.save(cinemaniaco);
    }

    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        cinemaniacoRepository.deleteById(id);
    }

    // ─── Social ─────────────────────────────────────────────────────────────

    @Transactional
    public boolean seguir(Long seguidorId, Long seguidoId) {
        if (seguidorId.equals(seguidoId)) throw new IllegalArgumentException("Un cinemaniaco no puede seguirse a sí mismo");

        Cinemaniaco seguidor = buscarPorId(seguidorId);
        Cinemaniaco seguido = buscarPorId(seguidoId);

        boolean resultado = seguidor.seguir(seguido);
        if (resultado) {
            cinemaniacoRepository.save(seguidor);
            cinemaniacoRepository.save(seguido);
        }
        return resultado;
    }

    @Transactional
    public boolean dejarDeSeguir(Long seguidorId, Long seguidoId) {
        Cinemaniaco seguidor = buscarPorId(seguidorId);
        Cinemaniaco seguido = buscarPorId(seguidoId);

        boolean resultado = seguidor.dejarDeSeguir(seguido);
        if (resultado) {
            cinemaniacoRepository.save(seguidor);
            cinemaniacoRepository.save(seguido);
        }
        return resultado;
    }

    public List<Cinemaniaco> obtenerSeguidos(Long id) {
        return buscarPorId(id).getSeguidos();
    }

    public List<Cinemaniaco> obtenerSeguidores(Long id) {
        return buscarPorId(id).getSeguidores();
    }

    public int contarSeguidores(Long id) {
        return buscarPorId(id).contarSeguidores();
    }

    public List<Cinemaniaco> amigosEnComun(Long idA, Long idB) {
        Cinemaniaco a = buscarPorId(idA);
        Cinemaniaco b = buscarPorId(idB);
        return a.amigosEnComun(b);
    }
}