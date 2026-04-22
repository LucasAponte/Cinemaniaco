package com.cinemaniaco.Cinemanico.service;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Persona;
import com.cinemaniaco.Cinemanico.exception.BusinessException;
import com.cinemaniaco.Cinemanico.exception.ResourceNotFoundException;
import com.cinemaniaco.Cinemanico.dto.request.CinemaniacoRequest;
import com.cinemaniaco.Cinemanico.dto.response.CinemaniacoResponse;
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

    public List<CinemaniacoResponse> listarTodos() {
        return cinemaniacoRepository.findAll()
                .stream().map(CinemaniacoResponse::from).toList();
    }

    public CinemaniacoResponse buscarPorIdDto(Long id) {
        return CinemaniacoResponse.from(buscarPorId(id));
    }

    public CinemaniacoResponse buscarPorApodoDto(String apodo) {
        return CinemaniacoResponse.from(buscarPorApodo(apodo));
    }

    @Transactional
    public CinemaniacoResponse registrar(CinemaniacoRequest request) {

        validarUnicidad(request);

        Persona persona = new Persona(
                request.getPersona().getEdad(),
                request.getPersona().getNombre(),
                request.getPersona().getApellido(),
                request.getPersona().getEmail()
        );
        Persona personaGuardada = personaRepository.save(persona);
        Cinemaniaco cinemaniaco = new Cinemaniaco(personaGuardada, request.getApodo());
        return CinemaniacoResponse.from(cinemaniacoRepository.save(cinemaniaco));
    }
    private void validarUnicidad(CinemaniacoRequest request) {

        String email = request.getPersona().getEmail();
        String apodo = request.getApodo();
        System.out.println(email +"  " +apodo);
        System.out.println(personaRepository.existsByEmail(email));
        if (personaRepository.existsByEmail(email)) {
            throw new BusinessException("Ya existe una persona con el email: " + email);
        }
        System.out.println(cinemaniacoRepository.existsByApodo(apodo));
        if (cinemaniacoRepository.existsByApodo(apodo)) {
            throw new BusinessException("El apodo ya está en uso: " + apodo);
        }
    }

    @Transactional
    public CinemaniacoResponse actualizarApodo(Long id, String nuevoApodo) {
        Cinemaniaco cinemaniaco = buscarPorId(id);
        cinemaniaco.setApodo(nuevoApodo);
        return CinemaniacoResponse.from(cinemaniacoRepository.save(cinemaniaco));
    }

    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        cinemaniacoRepository.deleteById(id);
    }

    // ─── Social ─────────────────────────────────────────────────────────────

    @Transactional
    public boolean seguir(Long seguidorId, Long seguidoId) {
        if (seguidorId.equals(seguidoId))
            throw new BusinessException("Un cinemaniaco no puede seguirse a sí mismo");

        Cinemaniaco seguidor = buscarPorId(seguidorId);
        Cinemaniaco seguido  = buscarPorId(seguidoId);

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
        Cinemaniaco seguido  = buscarPorId(seguidoId);

        boolean resultado = seguidor.dejarDeSeguir(seguido);
        if (resultado) {
            cinemaniacoRepository.save(seguidor);
            cinemaniacoRepository.save(seguido);
        }
        return resultado;
    }

    public List<CinemaniacoResponse> obtenerSeguidos(Long id) {
        return buscarPorId(id).getSeguidos()
                .stream().map(CinemaniacoResponse::from).toList();
    }

    public List<CinemaniacoResponse> obtenerSeguidores(Long id) {
        return buscarPorId(id).getSeguidores()
                .stream().map(CinemaniacoResponse::from).toList();
    }

    public int contarSeguidores(Long id) {
        return buscarPorId(id).contarSeguidores();
    }

    public List<CinemaniacoResponse> amigosEnComun(Long idA, Long idB) {
        return buscarPorId(idA).amigosEnComun(buscarPorId(idB))
                .stream().map(CinemaniacoResponse::from).toList();
    }

    // ─── Interno (para otros services) ──────────────────────────────────────

    public Cinemaniaco buscarPorId(Long id) {
        return cinemaniacoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinemaniaco no encontrado con id: " + id));
    }

    public Cinemaniaco buscarPorApodo(String apodo) {
        return cinemaniacoRepository.findByApodo(apodo)
                .orElseThrow(() -> new ResourceNotFoundException("Cinemaniaco no encontrado con apodo: " + apodo));
    }
}