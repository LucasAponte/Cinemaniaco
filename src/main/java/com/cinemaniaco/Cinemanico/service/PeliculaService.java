package com.cinemaniaco.Cinemanico.service;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Comentario;
import com.cinemaniaco.Cinemanico.exception.BusinessException;
import com.cinemaniaco.Cinemanico.exception.ResourceNotFoundException;
import com.cinemaniaco.Cinemanico.domain.Pelicula;
import com.cinemaniaco.Cinemanico.domain.Puntuacion;
import com.cinemaniaco.Cinemanico.dto.request.ComentarioRequest;
import com.cinemaniaco.Cinemanico.dto.request.PeliculaRequest;
import com.cinemaniaco.Cinemanico.dto.request.PuntuacionRequest;
import com.cinemaniaco.Cinemanico.dto.response.ComentarioResponse;
import com.cinemaniaco.Cinemanico.dto.response.PeliculaResponse;
import com.cinemaniaco.Cinemanico.dto.response.PuntuacionResponse;
import com.cinemaniaco.Cinemanico.repository.CinemaniacoRepository;
import com.cinemaniaco.Cinemanico.repository.ComentarioRepository;
import com.cinemaniaco.Cinemanico.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final CinemaniacoRepository cinemaniacoRepository;
    private final ComentarioRepository comentarioRepository;
    private final RestTemplate restTemplate;

    @Value("${openai.api.key:}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // ─── CRUD ───────────────────────────────────────────────────────────────

    public List<PeliculaResponse> listarTodas() {
        return peliculaRepository.findAll()
                .stream().map(PeliculaResponse::from).toList();
    }

    public PeliculaResponse buscarPorIdDto(Long id) {
        return PeliculaResponse.from(buscarPorId(id));
    }

    @Transactional
    public PeliculaResponse crear(PeliculaRequest request) {
        Pelicula pelicula = new Pelicula(
                request.getTitulo(),
                request.getDirector(),
                request.getAnioEstreno(),
                request.getActores(),
                request.getGeneros()
        );
        return PeliculaResponse.from(peliculaRepository.save(pelicula));
    }

    @Transactional
    public PeliculaResponse actualizar(Long id, PeliculaRequest request) {
        Pelicula pelicula = buscarPorId(id);
        pelicula.setTitulo(request.getTitulo());
        pelicula.setDirector(request.getDirector());
        pelicula.setAnioEstreno(request.getAnioEstreno());
        pelicula.setActores(request.getActores());
        pelicula.setGeneros(request.getGeneros());
        return PeliculaResponse.from(peliculaRepository.save(pelicula));
    }

    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        peliculaRepository.deleteById(id);
    }

    // ─── Puntuaciones ───────────────────────────────────────────────────────

    @Transactional
    public PeliculaResponse puntuarPelicula(Long peliculaId, PuntuacionRequest request) {
        Pelicula pelicula     = buscarPorId(peliculaId);
        Cinemaniaco cinemaniaco = buscarCinemaniaco(request.getCinemaniacoId());

        pelicula.anadirPuntuacion(cinemaniaco, request.getPuntos());
        peliculaRepository.save(pelicula);
        return PeliculaResponse.from(pelicula);
    }

    public double obtenerPromedio(Long peliculaId) {
        return buscarPorId(peliculaId).calcularPuntuacionPromedio();
    }

    public PuntuacionResponse obtenerPuntuacionDe(Long peliculaId, Long cinemaniacoId) {
        Pelicula pelicula       = buscarPorId(peliculaId);
        Cinemaniaco cinemaniaco = buscarCinemaniaco(cinemaniacoId);
        Puntuacion puntuacion   = pelicula.buscarPuntuacionPorCinemaniaco(cinemaniaco);
        if (puntuacion == null)
            throw new ResourceNotFoundException("El cinemaniaco aún no puntuó esta película");
        return PuntuacionResponse.from(puntuacion);
    }

    // ─── Comentarios ────────────────────────────────────────────────────────

    @Transactional
    public ComentarioResponse comentarPelicula(Long peliculaId, ComentarioRequest request) {
        Pelicula pelicula       = buscarPorId(peliculaId);
        Cinemaniaco cinemaniaco = buscarCinemaniaco(request.getCinemaniacoId());

        Comentario comentario = new Comentario(cinemaniaco, request.getTexto());
        comentarioRepository.save(comentario);
        pelicula.anadirComentario(comentario);
        peliculaRepository.save(pelicula);
        return ComentarioResponse.from(comentario);
    }

    @Transactional
    public ComentarioResponse responderComentario(Long comentarioPadreId, ComentarioRequest request) {
        Comentario padre        = buscarComentario(comentarioPadreId);
        Cinemaniaco cinemaniaco = buscarCinemaniaco(request.getCinemaniacoId());

        Comentario respuesta = new Comentario(cinemaniaco, request.getTexto());
        comentarioRepository.save(respuesta);
        padre.agregarComentario(respuesta);
        comentarioRepository.save(padre);
        return ComentarioResponse.from(respuesta);
    }

    @Transactional
    public ComentarioResponse darMeGusta(Long comentarioId, Long cinemaniacoId) {
        Comentario comentario   = buscarComentario(comentarioId);
        Cinemaniaco cinemaniaco = buscarCinemaniaco(cinemaniacoId);

        comentario.agregarMeGusta(cinemaniaco);
        comentarioRepository.save(comentario);
        return ComentarioResponse.from(comentario);
    }

    @Transactional
    public ComentarioResponse quitarMeGusta(Long comentarioId, Long cinemaniacoId) {
        Comentario comentario   = buscarComentario(comentarioId);
        Cinemaniaco cinemaniaco = buscarCinemaniaco(cinemaniacoId);

        comentario.quitarMeGustaDe(cinemaniaco);
        comentarioRepository.save(comentario);
        return ComentarioResponse.from(comentario);
    }

    // ─── Resumen IA ─────────────────────────────────────────────────────────

    @Transactional
    public String generarComentarioEnComunIA(Long peliculaId) {
        Pelicula pelicula = buscarPorId(peliculaId);

        List<String> textos = pelicula.getComentarios().stream()
                .map(Comentario::getTexto)
                .toList();

        if (textos.isEmpty())
            throw new BusinessException("La película no tiene comentarios aún para generar un resumen");

        if (openAiApiKey == null || openAiApiKey.isBlank())
            throw new BusinessException("La integración con OpenAI no está configurada en este entorno");

        String resumen = llamarOpenAI(construirPrompt(pelicula.getTitulo(), textos));
        pelicula.setComentarioComunidadIA(resumen);
        peliculaRepository.save(pelicula);
        return resumen;
    }

    public String obtenerComentarioEnComunIa(Long peliculaId) {
        Pelicula pelicula = buscarPorId(peliculaId);
        if (pelicula.getComentarioComunidadIA() == null)
            throw new BusinessException("Todavía no se generó un resumen para esta película");
        return pelicula.getComentarioComunidadIA();
    }

    // ─── Auxiliares internos ────────────────────────────────────────────────

    public Pelicula buscarPorId(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + id));
    }

    private Cinemaniaco buscarCinemaniaco(Long id) {
        return cinemaniacoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinemaniaco no encontrado con id: " + id));
    }

    private Comentario buscarComentario(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado con id: " + id));
    }

    private String construirPrompt(String titulo, List<String> comentarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sos un crítico de cine. A continuación te paso opiniones de usuarios sobre la película '")
                .append(titulo)
                .append("'. Hacé un resumen breve (máximo 3 oraciones) que capture el sentimiento general:\n\n");
        comentarios.forEach(c -> sb.append("- ").append(c).append("\n"));
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String llamarOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 300,
                "temperature", 0.7
        );

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_URL, HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers), Map.class
            );
            Map<String, Object> body = response.getBody();
            if (body == null) throw new BusinessException("Respuesta vacía de OpenAI");
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (HttpClientErrorException e) {
            String msg = switch (e.getStatusCode().value()) {
                case 401 -> "API key de OpenAI inválida o no autorizada";
                case 429 -> "Se superó el límite de solicitudes de OpenAI";
                case 400 -> "Solicitud malformada enviada a OpenAI";
                default  -> "Error del cliente al llamar a OpenAI: " + e.getStatusCode();
            };
            throw new BusinessException(msg);
        } catch (Exception e) {
            throw new BusinessException("Error inesperado al comunicarse con OpenAI: " + e.getMessage());
        }
    }
}