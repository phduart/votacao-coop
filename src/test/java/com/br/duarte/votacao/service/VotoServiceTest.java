package com.br.duarte.votacao.service;

import com.br.duarte.votacao.api.v1.dto.request.VotoRequest;
import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.entity.Voto;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
import com.br.duarte.votacao.domain.repository.VotoRepository;
import com.br.duarte.votacao.domain.service.SessaoService;
import com.br.duarte.votacao.domain.service.VotoService;
import com.br.duarte.votacao.utils.CpfUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private SessaoService sessaoService;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private CpfUtils cpfUtils;

    @InjectMocks
    private VotoService votoService;

    @Test
    void deveRegistrarVotoComSucesso() {
        // Arrange
        Long pautaId = 1L;
        String cpf = "12345678901";

        VotoRequest request = new VotoRequest(cpf, OpcaoVoto.SIM);

        Pauta pauta = new Pauta();
        SessaoVotacao sessao = mock(SessaoVotacao.class);

        when(sessao.isAberta()).thenReturn(true);
        when(sessao.getPauta()).thenReturn(pauta);
        when(sessaoService.getSessaoPorPautaId(pautaId)).thenReturn(sessao);
        when(votoRepository.existsByPautaIdAndCpf(pautaId, cpf)).thenReturn(false);

        // Act
        votoService.votar(pautaId, request);

        // Assert
        verify(cpfUtils).validarElegibilidade(cpf);

        ArgumentCaptor<Voto> votoCaptor = ArgumentCaptor.forClass(Voto.class);
        verify(votoRepository).save(votoCaptor.capture());

        Voto votoSalvo = votoCaptor.getValue();
        assertEquals(cpf, votoSalvo.getCpf());
        assertEquals(OpcaoVoto.SIM, votoSalvo.getVoto());
        assertEquals(pauta, votoSalvo.getPauta());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoEstiverEncerrada() {
        // Arrange
        Long pautaId = 1L;
        VotoRequest request = new VotoRequest("12345678901", OpcaoVoto.NAO);

        SessaoVotacao sessao = mock(SessaoVotacao.class);
        when(sessao.isAberta()).thenReturn(false);
        when(sessaoService.getSessaoPorPautaId(pautaId)).thenReturn(sessao);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> votoService.votar(pautaId, request)
        );

        assertEquals("A sessão de votação já está encerrada.", exception.getMessage());
        verify(votoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoJaTiverVotado() {
        // Arrange
        Long pautaId = 1L;
        String cpf = "12345678901";

        VotoRequest request = new VotoRequest(cpf, OpcaoVoto.SIM);

        SessaoVotacao sessao = mock(SessaoVotacao.class);
        when(sessao.isAberta()).thenReturn(true);
        when(sessaoService.getSessaoPorPautaId(pautaId)).thenReturn(sessao);
        when(votoRepository.existsByPautaIdAndCpf(pautaId, cpf)).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> votoService.votar(pautaId, request)
        );

        assertEquals("Associado já votou nesta pauta.", exception.getMessage());
        verify(votoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoForElegivel() {
        // Arrange
        Long pautaId = 1L;
        String cpf = "12345678901";

        VotoRequest request = new VotoRequest(cpf, OpcaoVoto.SIM);

        doThrow(new BusinessException("CPF não elegível"))
                .when(cpfUtils).validarElegibilidade(cpf);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> votoService.votar(pautaId, request)
        );

        assertEquals("CPF não elegível", exception.getMessage());
        verifyNoInteractions(votoRepository);
    }
}
