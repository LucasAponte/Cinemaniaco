package com.cinemaniaco.Cinemanico.service;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Comentario;
import com.cinemaniaco.Cinemanico.domain.Pelicula;
import com.cinemaniaco.Cinemanico.domain.Puntuacion;
import com.cinemaniaco.Cinemanico.repository.CinemaniacoRepository;
import com.cinemaniaco.Cinemanico.repository.ComentarioRepository;
import com.cinemaniaco.Cinemanico.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // ─── CRUD ───────────────────────────────────────────────────────────────

    public List<Pelicula> listarTodas() {
        return peliculaRepository.findAll();
    }

    public Pelicula buscarPorId(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada con id: " + id));
    }

    @Transactional
    public Pelicula crear(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public Pelicula actualizar(Long id, Pelicula datos) {
        Pelicula pelicula = buscarPorId(id);
        pelicula.setTitulo(datos.getTitulo());
        pelicula.setDirector(datos.getDirector());
        pelicula.setAnioEstreno(datos.getAnioEstreno());
        pelicula.setActores(datos.getActores());
        pelicula.setGeneros(datos.getGeneros());
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        peliculaRepository.deleteById(id);
    }

    // ─── Puntuaciones ───────────────────────────────────────────────────────

    @Transactional
    public double puntuarPelicula(Long peliculaId, Long cinemaniacoId, double puntos) {
        if (puntos < 0 || puntos > 10)
            throw new IllegalArgumentException("La puntuación debe estar entre 0 y 10");

        Pelicula pelicula = buscarPorId(peliculaId);
        Cinemaniaco cinemaniaco = cinemaniacoRepository.findById(cinemaniacoId)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado con id: " + cinemaniacoId));

        pelicula.anadirPuntuacion(cinemaniaco, puntos);
        peliculaRepository.save(pelicula);
        return pelicula.calcularPuntuacionPromedio();
    }

    public double obtenerPromedio(Long peliculaId) {
        return buscarPorId(peliculaId).calcularPuntuacionPromedio();
    }

    public Puntuacion obtenerPuntuacionDe(Long peliculaId, Long cinemaniacoId) {
        Pelicula pelicula = buscarPorId(peliculaId);
        Cinemaniaco cinemaniaco = cinemaniacoRepository.findById(cinemaniacoId)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado"));
        return pelicula.buscarPuntuacionPorCinemaniaco(cinemaniaco);
    }

    // ─── Comentarios ────────────────────────────────────────────────────────

    @Transactional
    public Comentario comentarPelicula(Long peliculaId, Long cinemaniacoId, String texto) {
        Pelicula pelicula = buscarPorId(peliculaId);
        Cinemaniaco cinemaniaco = cinemaniacoRepository.findById(cinemaniacoId)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado"));

        Comentario comentario = new Comentario(cinemaniaco, texto);
        comentarioRepository.save(comentario);
        pelicula.anadirComentario(comentario);
        peliculaRepository.save(pelicula);
        return comentario;
    }

    @Transactional
    public Comentario responderComentario(Long comentarioPadreId, Long cinemaniacoId, String texto) {
        Comentario padre = comentarioRepository.findById(comentarioPadreId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        Cinemaniaco cinemaniaco = cinemaniacoRepository.findById(cinemaniacoId)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado"));

        Comentario respuesta = new Comentario(cinemaniaco, texto);
        comentarioRepository.save(respuesta);
        padre.agregarComentario(respuesta);
        comentarioRepository.save(padre);
        return respuesta;
    }

    @Transactional
    public int darMeGusta(Long comentarioId, Long cinemaniacoId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        Cinemaniaco cinemaniaco = cinemaniacoRepository.findById(cinemaniacoId)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado"));

        comentario.agregarMeGusta(cinemaniaco);
        comentarioRepository.save(comentario);
        return comentario.getMeGusta();
    }

    @Transactional
    public int quitarMeGusta(Long comentarioId, Long cinemaniacoId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        Cinemaniaco cinemaniaco = cinemaniacoRepository.findById(cinemaniacoId)
                .orElseThrow(() -> new RuntimeException("Cinemaniaco no encontrado"));

        comentario.quitarMeGustaDe(cinemaniaco);
        comentarioRepository.save(comentario);
        return comentario.getMeGusta();
    }

    // ─── Resumen IA ─────────────────────────────────────────────────────────

    @Transactional
    public String generarResumenIA(Long peliculaId) {
        Pelicula pelicula = buscarPorId(peliculaId);

        List<String> textos = pelicula.getComentarios().stream()
                .map(Comentario::getTexto)
                .toList();

        if (textos.isEmpty())
            throw new RuntimeException("La película no tiene comentarios aún para generar un resumen");

        String prompt = construirPrompt(pelicula.getTitulo(), textos);
        String resumen = llamarOpenAI(prompt);

        pelicula.setResumenIA(resumen);
        peliculaRepository.save(pelicula);
        return resumen;
    }

    public String obtenerResumenIA(Long peliculaId) {
        Pelicula pelicula = buscarPorId(peliculaId);
        if (pelicula.getResumenIA() == null)
            throw new RuntimeException("Todavía no se generó un resumen para esta película");
        return pelicula.getResumenIA();
    }

    // ─── Auxiliares OpenAI ──────────────────────────────────────────────────

    private String construirPrompt(String tituloPelicula, List<String> comentarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sos un crítico de cine. A continuación te paso opiniones de usuarios sobre la película '")
                .append(tituloPelicula)
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
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 300,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) throw new RuntimeException("Respuesta vacía de OpenAI");

            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con OpenAI: " + e.getMessage(), e);
        }
    }
}