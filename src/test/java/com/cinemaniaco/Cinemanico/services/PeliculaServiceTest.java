package com.cinemaniaco.Cinemanico.services;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Comentario;
import com.cinemaniaco.Cinemanico.domain.Pelicula;
import com.cinemaniaco.Cinemanico.domain.Persona;
import com.cinemaniaco.Cinemanico.dto.request.ComentarioRequest;
import com.cinemaniaco.Cinemanico.dto.request.PeliculaRequest;
import com.cinemaniaco.Cinemanico.dto.request.PuntuacionRequest;
import com.cinemaniaco.Cinemanico.dto.response.ComentarioResponse;
import com.cinemaniaco.Cinemanico.dto.response.PeliculaResponse;
import com.cinemaniaco.Cinemanico.exception.BusinessException;
import com.cinemaniaco.Cinemanico.exception.ResourceNotFoundException;
import com.cinemaniaco.Cinemanico.repository.CinemaniacoRepository;
import com.cinemaniaco.Cinemanico.repository.ComentarioRepository;
import com.cinemaniaco.Cinemanico.repository.PeliculaRepository;
import com.cinemaniaco.Cinemanico.service.PeliculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PeliculaService")
class PeliculaServiceTest {

    @Mock private PeliculaRepository peliculaRepository;
    @Mock private CinemaniacoRepository cinemaniacoRepository;
    @Mock private ComentarioRepository comentarioRepository;
    @Mock private RestTemplate restTemplate;

    @InjectMocks private PeliculaService service;

    // ─── Fixtures ────────────────────────────────────────────────────────────

    private Pelicula pelicula;
    private Cinemaniaco cinemaniaco;

    @BeforeEach
    void setUp() {
        Persona persona = new Persona(1L, "Juan", 25, "Pérez", "juan@mail.com");
        cinemaniaco = new Cinemaniaco(1L, persona, "juancho", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());
        pelicula = new Pelicula(
                "Inception", "Christopher Nolan", 2010,
                List.of("Leonardo DiCaprio"), List.of("Ciencia ficción")
        );
    }

    // ─── listarTodas ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("listarTodas")
    class ListarTodas {

        @Test
        @DisplayName("devuelve lista vacía si no hay películas")
        void listarTodasDevuelveListaVaciaDevuelveValorCorrecto() {
            when(peliculaRepository.findAll()).thenReturn(List.of());

            assertThat(service.listarTodas()).isEmpty();
        }

        @Test
        @DisplayName("devuelve todas las películas mapeadas a DTO")
        void listarTodasDevuelveTodasLasPeliculasDevuelveValorCorrecto() {
            when(peliculaRepository.findAll()).thenReturn(List.of(pelicula));

            List<PeliculaResponse> resultado = service.listarTodas();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getTitulo()).isEqualTo("Inception");
        }
    }

    // ─── buscarPorId ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("lanza ResourceNotFoundException si no existe")
        void buscarPorIdLanzaResourceNotFoundDevuelveValorCorrecto() {
            when(peliculaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("retorna la película si existe")
        void buscarPorIdRetornaPeliculaExistenteDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            Pelicula resultado = service.buscarPorId(1L);

            assertThat(resultado.getTitulo()).isEqualTo("Inception");
        }
    }

    // ─── crear ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crear")
    class Crear {

        @Test
        @DisplayName("crea y guarda la película correctamente")
        void crearCreaYPersistePeliculaDevuelveValorCorrecto() {
            PeliculaRequest request = new PeliculaRequest(
                    "Inception", "Christopher Nolan", 2010,
                    List.of("Leonardo DiCaprio"), List.of("Ciencia ficción")
            );
            when(peliculaRepository.save(any(Pelicula.class))).thenReturn(pelicula);

            PeliculaResponse resultado = service.crear(request);

            assertThat(resultado.getTitulo()).isEqualTo("Inception");
            verify(peliculaRepository).save(any(Pelicula.class));
        }
    }

    // ─── actualizar ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("actualiza los campos y guarda")
        void actualizarActualizaCamposYGuardaDevuelveValorCorrecto() {
            PeliculaRequest request = new PeliculaRequest(
                    "Inception 2", "Nolan", 2025,
                    List.of("Actor"), List.of("Acción")
            );
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(peliculaRepository.save(any())).thenReturn(pelicula);

            service.actualizar(1L, request);

            assertThat(pelicula.getTitulo()).isEqualTo("Inception 2");
            assertThat(pelicula.getAnioEstreno()).isEqualTo(2025);
            verify(peliculaRepository).save(pelicula);
        }

        @Test
        @DisplayName("lanza excepción si la película no existe")
        void actualizarLanzaExcepcionSiNoExisteDevuelveValorCorrecto() {
            when(peliculaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizar(99L, new PeliculaRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── eliminar ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("elimina si la película existe")
        void eliminarEliminaSiExisteDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            service.eliminar(1L);

            verify(peliculaRepository).deleteById(1L);
        }

        @Test
        @DisplayName("lanza excepción y no elimina si no existe")
        void eliminarLanzaExcepcionSiNoExisteDevuelveValorCorrecto() {
            when(peliculaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(peliculaRepository, never()).deleteById(any());
        }
    }

    // ─── puntuarPelicula ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("puntuarPelicula")
    class PuntuarPelicula {

        @Test
        @DisplayName("agrega puntuación y devuelve el nuevo promedio")
        void puntuarPeliculaAgregaPuntuacionDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(peliculaRepository.save(any())).thenReturn(pelicula);

            double promedio = service.puntuarPelicula(1L, new PuntuacionRequest(1L, 8.0));

            assertThat(promedio).isEqualTo(8.0);
        }

        @Test
        @DisplayName("actualiza puntuación existente del mismo cinemaniaco")
        void puntuarPeliculaActualizaPuntuacionExistenteDevuelveValorCorrecto() {
            pelicula.anadirPuntuacion(cinemaniaco, 6.0);

            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(peliculaRepository.save(any())).thenReturn(pelicula);

            double promedio = service.puntuarPelicula(1L, new PuntuacionRequest(1L, 9.0));

            assertThat(pelicula.cantPuntuaciones()).isEqualTo(1); // sigue siendo 1, no duplicó
            assertThat(promedio).isEqualTo(9.0);
        }

        @Test
        @DisplayName("lanza ResourceNotFoundException si la película no existe")
        void puntuarPeliculaLanzaExcepcionSiPeliculaNoExisteDevuelveValorCorrecto() {
            when(peliculaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.puntuarPelicula(99L, new PuntuacionRequest(1L, 7.0)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── obtenerPromedio ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("obtenerPromedio")
    class ObtenerPromedio {

        @Test
        @DisplayName("devuelve 0.0 si no hay puntuaciones")
        void obtenerPromedioDevuelveCeroSinPuntuacionesDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            assertThat(service.obtenerPromedio(1L)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("devuelve el promedio correcto con varias puntuaciones")
        void obtenerPromedioDevuelvePromedioCorrectoDevuelveValorCorrecto() {
            Persona p2 = new Persona(2L, "Ana", 22, "García", "ana@mail.com");
            Cinemaniaco c2 = new Cinemaniaco(2L, p2, "anita", new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>());
            pelicula.anadirPuntuacion(cinemaniaco, 8.0);
            pelicula.anadirPuntuacion(c2, 6.0);

            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            assertThat(service.obtenerPromedio(1L)).isEqualTo(7.0);
        }
    }

    // ─── comentarPelicula ────────────────────────────────────────────────────

    @Nested
    @DisplayName("comentarPelicula")
    class ComentarPelicula {

        @Test
        @DisplayName("agrega comentario y lo devuelve como DTO")
        void comentarPeliculaAgregaComentarioDevuelveValorCorrecto() {
            ComentarioRequest request = new ComentarioRequest(1L, "Excelente película");

            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(comentarioRepository.save(any(Comentario.class))).thenAnswer(i -> i.getArgument(0));
            when(peliculaRepository.save(any())).thenReturn(pelicula);

            ComentarioResponse resultado = service.comentarPelicula(1L, request);

            assertThat(resultado.getTexto()).isEqualTo("Excelente película");
            assertThat(resultado.getApodoCinemaniaco()).isEqualTo("juancho");
            assertThat(pelicula.getComentarios()).hasSize(1);
        }

        @Test
        @DisplayName("lanza excepción si el cinemaniaco no existe")
        void comentarPeliculaLanzaExcepcionSiCinemaniacoNoExisteDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(cinemaniacoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.comentarPelicula(1L, new ComentarioRequest(99L, "texto")))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── darMeGusta / quitarMeGusta ──────────────────────────────────────────

    @Nested
    @DisplayName("meGusta")
    class MeGusta {

        private Comentario comentario;

        @BeforeEach
        void setUp() {
            comentario = new Comentario(cinemaniaco, "Muy buena");
        }

        @Test
        @DisplayName("incrementa el contador al dar me gusta")
        void meGustaIncrementaContadorDevuelveValorCorrecto() {
            Persona p2 = new Persona(2L, "Ana", 22, "García", "ana@mail.com");
            Cinemaniaco c2 = new Cinemaniaco(2L, p2, "anita", new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>());

            when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(c2));
            when(comentarioRepository.save(any())).thenReturn(comentario);

            int total = service.darMeGusta(1L, 2L);

            assertThat(total).isEqualTo(1);
        }

        @Test
        @DisplayName("no duplica me gusta si ya lo dio el mismo cinemaniaco")
        void meGustaNoDuplicaSiYaDioDevuelveValorCorrecto() {
            comentario.agregarMeGusta(cinemaniaco); // ya tiene 1

            when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(comentarioRepository.save(any())).thenReturn(comentario);

            int total = service.darMeGusta(1L, 1L);

            assertThat(total).isEqualTo(1); // sigue siendo 1
        }

        @Test
        @DisplayName("decrementa el contador al quitar me gusta")
        void meGustaDecrementaContadorDevuelveValorCorrecto() {
            comentario.agregarMeGusta(cinemaniaco);

            when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(comentarioRepository.save(any())).thenReturn(comentario);

            int total = service.quitarMeGusta(1L, 1L);

            assertThat(total).isEqualTo(0);
        }

        @Test
        @DisplayName("no hace nada si quita me gusta sin haberlo dado")
        void meGustaNoHaceNadaSiQuitaSinHaberDadoDevuelveValorCorrecto() {
            Persona p2 = new Persona(2L, "Ana", 22, "García", "ana@mail.com");
            Cinemaniaco c2 = new Cinemaniaco(2L, p2, "anita", new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>());

            when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(c2));
            when(comentarioRepository.save(any())).thenReturn(comentario);

            int total = service.quitarMeGusta(1L, 2L);

            assertThat(total).isEqualTo(0);
        }
    }

    // ─── generarResumenIA ────────────────────────────────────────────────────

    @Nested
    @DisplayName("generarResumenIA")
    class GenerarResumenIA {

        @Test
        @DisplayName("lanza BusinessException si la película no tiene comentarios")
        void generarResumenIALanzaExcepcionSiNoTieneComentariosDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            assertThatThrownBy(() -> service.generarResumenIA(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("comentarios");
        }

        @Test
        @DisplayName("llama a OpenAI, guarda y retorna el resumen")
        @SuppressWarnings("unchecked")
        void generarResumenIALlamaOpenAISalvaYRetornaDevuelveValorCorrecto() {
            pelicula.anadirComentario(new Comentario(cinemaniaco, "Muy buena"));
            pelicula.anadirComentario(new Comentario(cinemaniaco, "Me encantó el final"));

            Map<String, Object> openAiResponse = Map.of(
                    "choices", List.of(
                            Map.of("message", Map.of("content", "Resumen generado por IA"))
                    )
            );

            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                    .thenReturn(new ResponseEntity<>(openAiResponse, HttpStatus.OK));
            when(peliculaRepository.save(any())).thenReturn(pelicula);

            String resultado = service.generarResumenIA(1L);

            assertThat(resultado).isEqualTo("Resumen generado por IA");
            assertThat(pelicula.getResumenIA()).isEqualTo("Resumen generado por IA");
            verify(peliculaRepository).save(pelicula);
        }

        @Test
        @DisplayName("lanza BusinessException si OpenAI devuelve respuesta vacía")
        @SuppressWarnings("unchecked")
        void generarResumenIALanzaExcepcionSiOpenAIFallaDevuelveValorCorrecto() {
            pelicula.anadirComentario(new Comentario(cinemaniaco, "Comentario"));

            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));
            when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Map.class)))
                    .thenReturn(ResponseEntity.ok((Map) null));

            assertThatThrownBy(() -> service.generarResumenIA(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ─── obtenerResumenIA ────────────────────────────────────────────────────

    @Nested
    @DisplayName("obtenerResumenIA")
    class ObtenerResumenIA {

        @Test
        @DisplayName("retorna el resumen si ya fue generado")
        void obtenerResumenIARetornaResumenPreexistenteDevuelveValorCorrecto() {
            pelicula.setResumenIA("Resumen previo");
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            String resultado = service.obtenerResumenIA(1L);

            assertThat(resultado).isEqualTo("Resumen previo");
        }

        @Test
        @DisplayName("lanza BusinessException si no se generó resumen aún")
        void obtenerResumenIALanzaExcepcionSiNoHayResumenDevuelveValorCorrecto() {
            when(peliculaRepository.findById(1L)).thenReturn(Optional.of(pelicula));

            assertThatThrownBy(() -> service.obtenerResumenIA(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("resumen");
        }
    }
}