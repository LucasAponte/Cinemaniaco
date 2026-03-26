package com.cinemaniaco.Cinemanico.services;

import com.cinemaniaco.Cinemanico.domain.Cinemaniaco;
import com.cinemaniaco.Cinemanico.domain.Persona;
import com.cinemaniaco.Cinemanico.dto.request.CinemaniacoRequest;
import com.cinemaniaco.Cinemanico.dto.request.PersonaRequest;
import com.cinemaniaco.Cinemanico.dto.response.CinemaniacoResponse;
import com.cinemaniaco.Cinemanico.exception.BusinessException;
import com.cinemaniaco.Cinemanico.exception.ResourceNotFoundException;
import com.cinemaniaco.Cinemanico.repository.CinemaniacoRepository;
import com.cinemaniaco.Cinemanico.repository.PersonaRepository;
import com.cinemaniaco.Cinemanico.service.CinemaniacoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CinemaniacoService")
class CinemaniacoServiceTest {

    @Mock private CinemaniacoRepository cinemaniacoRepository;
    @Mock private PersonaRepository personaRepository;

    @InjectMocks private CinemaniacoService service;

    // ─── Fixtures ────────────────────────────────────────────────────────────

    private Persona persona;
    private Cinemaniaco cinemaniaco;

    @BeforeEach
    void setUp() {
        persona = new Persona(1L, "Juan", 25, "Pérez", "juan@mail.com");
        cinemaniaco = new Cinemaniaco(1L, persona, "juancho", new java.util.ArrayList<>(),
                new java.util.ArrayList<>(), new java.util.ArrayList<>());
    }

    // ─── listarTodos ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("devuelve lista vacía cuando no hay cinemaniacos")
        void listarTodosDevuelveListaVaciaDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findAll()).thenReturn(List.of());

            List<CinemaniacoResponse> resultado = service.listarTodos();

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("devuelve todos los cinemaniacos mapeados a DTO")
        void listarTodosDevuelveTodosLosCinemaniacosDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findAll()).thenReturn(List.of(cinemaniaco));

            List<CinemaniacoResponse> resultado = service.listarTodos();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getApodo()).isEqualTo("juancho");
        }
    }

    // ─── buscarPorId ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("lanza ResourceNotFoundException si no existe")
        void buscarPorIdLanzaExcepcionDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("retorna el cinemaniaco si existe")
        void buscarPorIdRetornaCinemaniacoDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));

            Cinemaniaco resultado = service.buscarPorId(1L);

            assertThat(resultado.getApodo()).isEqualTo("juancho");
        }
    }

    // ─── buscarPorApodo ──────────────────────────────────────────────────────
    @Nested
    @DisplayName("buscarPorApodo")
    class BuscarPorApodo {

        @Test
        @DisplayName("lanza ResourceNotFoundException si el apodo no existe")
        void buscarPorApodoLanzaExcepcionDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findByApodo("fantasma")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorApodo("fantasma"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("fantasma");
        }

        @Test
        @DisplayName("retorna el cinemaniaco si el apodo existe")
        void buscarPorApodoRetornaCinemaniacoDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findByApodo("juancho")).thenReturn(Optional.of(cinemaniaco));

            Cinemaniaco resultado = service.buscarPorApodo("juancho");

            assertThat(resultado.getId_Cinemaniaco()).isEqualTo(1L);
        }
    }

    // ─── registrar ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("guarda persona y cinemaniaco y devuelve el DTO")
        void registrarGuardaPersonaYCinemaniacoDevuelveValorCorrecto() {
            CinemaniacoRequest request = new CinemaniacoRequest(
                    new PersonaRequest("Juan", "Pérez", 25, "juan@mail.com"),
                    "juancho"
            );

            when(personaRepository.save(any(Persona.class))).thenReturn(persona);
            when(cinemaniacoRepository.save(any(Cinemaniaco.class))).thenReturn(cinemaniaco);

            CinemaniacoResponse resultado = service.registrar(request);

            assertThat(resultado.getApodo()).isEqualTo("juancho");
            verify(personaRepository).save(any(Persona.class));
            verify(cinemaniacoRepository).save(any(Cinemaniaco.class));
        }
    }

    // ─── actualizarApodo ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("actualizarApodo")
    class ActualizarApodo {

        @Test
        @DisplayName("actualiza el apodo y guarda")
        void actualizarApodoActualizaYGuardaDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.save(any())).thenReturn(cinemaniaco);

            service.actualizarApodo(1L, "nuevoApodo");

            assertThat(cinemaniaco.getApodo()).isEqualTo("nuevoApodo");
            verify(cinemaniacoRepository).save(cinemaniaco);
        }

        @Test
        @DisplayName("lanza excepción si no existe el cinemaniaco")
        void actualizarApodoLanzaExcepcionSiNoExisteDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarApodo(99L, "nuevo"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── eliminar ────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("elimina si el cinemaniaco existe")
        void eliminarEliminaSiExisteDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));

            service.eliminar(1L);

            verify(cinemaniacoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("lanza excepción si no existe")
        void eliminarLanzaExcepcionSiNoExisteDevuelveValorCorrecto() {
            when(cinemaniacoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(cinemaniacoRepository, never()).deleteById(any());
        }
    }

    // ─── seguir ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("seguir")
    class Seguir {

        @Test
        @DisplayName("lanza BusinessException si intenta seguirse a sí mismo")
        void seguirLanzaBusinessExceptionSiSeSigueSiMismoDevuelveValorCorrecto() {
            assertThatThrownBy(() -> service.seguir(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("sí mismo");
        }

        @Test
        @DisplayName("retorna true y guarda ambos cuando el seguimiento es exitoso")
        void seguirRetornaTrueYGuardaAmbosDevuelveValorCorrecto() {
            Cinemaniaco seguido = new Cinemaniaco(2L, persona, "otro", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());

            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(seguido));
            when(cinemaniacoRepository.save(any())).thenReturn(cinemaniaco);

            boolean resultado = service.seguir(1L, 2L);

            assertThat(resultado).isTrue();
            verify(cinemaniacoRepository, times(2)).save(any());
        }

        @Test
        @DisplayName("retorna false y no guarda si ya lo seguía")
        void seguirRetornaFalseSiYaLoSeguiaDevuelveValorCorrecto() {
            Cinemaniaco seguido = new Cinemaniaco(2L, persona, "otro", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());
            cinemaniaco.getSeguidos().add(seguido); // ya lo sigue

            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(seguido));

            boolean resultado = service.seguir(1L, 2L);

            assertThat(resultado).isFalse();
            verify(cinemaniacoRepository, never()).save(any());
        }
    }

    // ─── dejarDeSeguir ───────────────────────────────────────────────────────
    @Nested
    @DisplayName("dejarDeSeguir")
    class DejarDeSeguir {

        @Test
        @DisplayName("retorna true si dejó de seguir exitosamente")
        void dejarDeSeguirRetornaTrueSiDejoDeSeguirDevuelveValorCorrecto() {
            Cinemaniaco seguido = new Cinemaniaco(2L, persona, "otro", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());
            cinemaniaco.getSeguidos().add(seguido);

            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(seguido));
            when(cinemaniacoRepository.save(any())).thenReturn(cinemaniaco);

            boolean resultado = service.dejarDeSeguir(1L, 2L);

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("retorna false si no lo seguía")
        void dejarDeSeguirRetornaFalseSiNoLoSeguiaDevuelveValorCorrecto() {
            Cinemaniaco seguido = new Cinemaniaco(2L, persona, "otro", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());

            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(seguido));

            boolean resultado = service.dejarDeSeguir(1L, 2L);

            assertThat(resultado).isFalse();
            verify(cinemaniacoRepository, never()).save(any());
        }
    }

    // ─── amigosEnComun ───────────────────────────────────────────────────────
    @Nested
    @DisplayName("amigosEnComun")
    class AmigosEnComun {

        @Test
        @DisplayName("retorna la intersección de seguidos entre dos cinemaniacos")
        void amigosEnComunRetornaInterseccionDevuelveValorCorrecto() {
            Cinemaniaco enComun = new Cinemaniaco(3L, persona, "encomun", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());
            Cinemaniaco otro = new Cinemaniaco(2L, persona, "otro", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());

            cinemaniaco.getSeguidos().add(enComun);
            otro.getSeguidos().add(enComun);

            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(otro));

            List<CinemaniacoResponse> resultado = service.amigosEnComun(1L, 2L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getApodo()).isEqualTo("encomun");
        }

        @Test
        @DisplayName("retorna lista vacía si no hay seguidos en común")
        void amigosEnComunRetornaVacioDevuelveValorCorrecto() {
            Cinemaniaco otro = new Cinemaniaco(2L, persona, "otro", new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), new java.util.ArrayList<>());

            when(cinemaniacoRepository.findById(1L)).thenReturn(Optional.of(cinemaniaco));
            when(cinemaniacoRepository.findById(2L)).thenReturn(Optional.of(otro));

            List<CinemaniacoResponse> resultado = service.amigosEnComun(1L, 2L);

            assertThat(resultado).isEmpty();
        }
    }
}
