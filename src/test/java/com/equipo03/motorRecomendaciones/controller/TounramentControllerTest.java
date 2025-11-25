package com.equipo03.motorRecomendaciones.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.equipo03.motorRecomendaciones.controller.TournamentController;
import com.equipo03.motorRecomendaciones.dto.response.TournamentResponseDTO;
import com.equipo03.motorRecomendaciones.service.TournamentService;


@WebMvcTest(TournamentController.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    private TournamentResponseDTO sampleTournamentDTO;

    @BeforeEach
    void setUp() {
        sampleTournamentDTO = new TournamentResponseDTO(
                1L,
                "Torneo Test",
                "Valorant",
                "UPCOMING",
                0);
    }

    @Test
    void getTournaments_devuelveListaCorrecta() throws Exception {
        when(tournamentService.getTournaments(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(sampleTournamentDTO));

        mockMvc.perform(get("/api/tournaments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Torneo Test"))
                .andExpect(jsonPath("$[0].game").value("Valorant"))
                .andExpect(jsonPath("$[0].status").value("UPCOMING"))
                .andExpect(jsonPath("$[0].participants").value(0));

        verify(tournamentService).getTournaments(null, null, null, null, null, null);
    }
}
