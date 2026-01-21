package com.br.duarte.votacao.service;

import com.br.duarte.votacao.api.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.api.dto.response.VotoContagem;
import com.br.duarte.votacao.api.exception.ResourceNotFoundException;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.repository.VotoRepository;
import com.br.duarte.votacao.domain.service.VotacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotacaoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private VotacaoService votacaoService;

    @Test
    void deveLancarExcecaoQuandoPautaNaoExistir() {
        // Arrange
        Long pautaId = 1L;
        when(pautaRepository.existsById(pautaId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> votacaoService.apurar(pautaId)
        );

        assertEquals("Pauta não encontrada com ID: " + pautaId, exception.getMessage());
        verify(votoRepository, never()).contarVotosAgrupados(any());
    }

    @Test
    void deveApurarVotosComSucesso() {
        // Arrange
        Long pautaId = 1L;

        when(pautaRepository.existsById(pautaId)).thenReturn(true);

        List<VotoContagem> contagens = List.of(
                new VotoContagem(OpcaoVoto.SIM, 5L),
                new VotoContagem(OpcaoVoto.NAO, 3L)
        );

        when(votoRepository.contarVotosAgrupados(pautaId)).thenReturn(contagens);

        // Act
        ResultadoVotacaoResponse response = votacaoService.apurar(pautaId);

        // Assert
        assertNotNull(response);
        assertEquals(pautaId, response.pautaId());

        Map<OpcaoVoto, Long> resultado = response.resultado();
        assertEquals(5L, resultado.get(OpcaoVoto.SIM));
        assertEquals(3L, resultado.get(OpcaoVoto.NAO));
    }

    @Test
    void deveRetornarZeroParaOpcaoSemVotos() {
        // Arrange
        Long pautaId = 1L;

        when(pautaRepository.existsById(pautaId)).thenReturn(true);

        // Apenas votos SIM no banco
        List<VotoContagem> contagens = List.of(
                new VotoContagem(OpcaoVoto.SIM, 10L)
        );

        when(votoRepository.contarVotosAgrupados(pautaId)).thenReturn(contagens);

        // Act
        ResultadoVotacaoResponse response = votacaoService.apurar(pautaId);

        // Assert
        Map<OpcaoVoto, Long> resultado = response.resultado();

        assertEquals(10L, resultado.get(OpcaoVoto.SIM));
        assertEquals(0L, resultado.get(OpcaoVoto.NAO)); // garante preenchimento do EnumMap
    }

    @Test
    void deveRetornarZeroParaTodasAsOpcoesQuandoNaoExistiremVotos() {
        // Arrange
        Long pautaId = 1L;

        when(pautaRepository.existsById(pautaId)).thenReturn(true);
        when(votoRepository.contarVotosAgrupados(pautaId)).thenReturn(List.of());

        // Act
        ResultadoVotacaoResponse response = votacaoService.apurar(pautaId);

        // Assert
        Map<OpcaoVoto, Long> resultado = response.resultado();

        for (OpcaoVoto opcao : OpcaoVoto.values()) {
            assertEquals(0L, resultado.get(opcao));
        }
    }
}