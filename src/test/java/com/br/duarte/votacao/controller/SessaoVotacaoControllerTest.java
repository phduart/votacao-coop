package com.br.duarte.votacao.controller;

import com.br.duarte.votacao.api.v1.controller.impl.SessaoVotacaoController;
import com.br.duarte.votacao.api.v1.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.v1.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.domain.enums.StatusSessao;
import com.br.duarte.votacao.domain.service.SessaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessaoVotacaoController.class)
class SessaoVotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessaoService sessaoService;

    @Autowired
    private ObjectMapper objectMapper;

    private SessaoVotacaoResponse mockResponse() {
        return new SessaoVotacaoResponse(
                1L,
                10L,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusMinutes(1),
                StatusSessao.ABERTA
        );
    }

    @Test
    @DisplayName("Deve abrir sessão com sucesso quando body não for enviado")
    void deveAbrirSessaoSemBody() throws Exception {

        when(sessaoService.abrirSessao(eq(10L), any()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", 10L))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve abrir sessão com sucesso quando minutos for informado")
    void deveAbrirSessaoComMinutos() throws Exception {

        AbrirSessaoRequest request = new AbrirSessaoRequest(5);

        when(sessaoService.abrirSessao(eq(10L), any()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar BadRequest quando minutos for zero")
    void deveRetornarBadRequestQuandoMinutosForZero() throws Exception {

        AbrirSessaoRequest request = new AbrirSessaoRequest(0);

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar BadRequest quando minutos for negativo")
    void deveRetornarBadRequestQuandoMinutosForNegativo() throws Exception {

        AbrirSessaoRequest request = new AbrirSessaoRequest(-3);

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}