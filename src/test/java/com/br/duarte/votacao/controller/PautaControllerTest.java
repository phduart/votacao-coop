package com.br.duarte.votacao.controller;

import com.br.duarte.votacao.api.v1.controller.impl.PautaController;
import com.br.duarte.votacao.api.v1.dto.response.PautaResponse;
import com.br.duarte.votacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.domain.service.PautaService;
import com.br.duarte.votacao.domain.service.VotacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private VotacaoService votacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarPautaComSucesso() throws Exception {
        // Arrange
        PautaResponse response = new PautaResponse();
        response.setId(1L);
        response.setTitulo("Aumento do VR");
        response.setDescricao("Reajuste de 10%");
        response.setCriadaEm(OffsetDateTime.now());

        when(pautaService.salvar(any())).thenReturn(response);

        String json = """
            {
              "titulo": "Aumento do VR",
              "descricao": "Reajuste de 10%"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Aumento do VR"))
                .andExpect(jsonPath("$.descricao").value("Reajuste de 10%"));
    }

    @Test
    void deveRetornarBadRequestQuandoTituloNaoForInformado() throws Exception {
        // Arrange
        String json = """
            {
              "descricao": "Reajuste de 10%"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pautaService);
    }

    @Test
    void deveRetornarBadRequestQuandoDescricaoNaoForInformada() throws Exception {
        // Arrange
        String json = """
            {
              "titulo": "Aumento do VR"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pautaService);
    }

    @Test
    void deveRetornarResultadoDaVotacao() throws Exception {
        // Arrange
        ResultadoVotacaoResponse response =
                new ResultadoVotacaoResponse(1L, Map.of());

        when(votacaoService.apurar(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pautas/{pautaId}/resultado", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(1L));
    }
}