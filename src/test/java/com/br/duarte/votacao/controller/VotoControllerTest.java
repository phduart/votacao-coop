package com.br.duarte.votacao.controller;

import com.br.duarte.votacao.api.controller.impl.VotoController;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotoService votoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRegistrarVotoComSucesso() throws Exception {
        // Arrange
        Long pautaId = 1L;

        String jsonRequest = """
            {
              "cpf": "12345678901",
              "voto": "SIM"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());

        verify(votoService).votar(pautaId,
                new com.br.duarte.votacao.api.dto.request.VotoRequest(
                        "12345678901",
                        OpcaoVoto.SIM
                )
        );
    }

    @Test
    void deveRetornarBadRequestQuandoCpfForInvalido() throws Exception {
        // Arrange
        Long pautaId = 1L;

        String jsonRequest = """
            {
              "cpf": "123",
              "voto": "SIM"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(votoService);
    }

    @Test
    void deveRetornarBadRequestQuandoCpfNaoForInformado() throws Exception {
        // Arrange
        Long pautaId = 1L;

        String jsonRequest = """
            {
              "voto": "SIM"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(votoService);
    }

    @Test
    void deveRetornarBadRequestQuandoVotoNaoForInformado() throws Exception {
        // Arrange
        Long pautaId = 1L;

        String jsonRequest = """
            {
              "cpf": "12345678901"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(votoService);
    }

    @Test
    void deveRetornarBadRequestQuandoVotoForInvalido() throws Exception {
        // Arrange
        Long pautaId = 1L;

        String jsonRequest = """
            {
              "cpf": "12345678901",
              "voto": "TALVEZ"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(votoService);
    }
}