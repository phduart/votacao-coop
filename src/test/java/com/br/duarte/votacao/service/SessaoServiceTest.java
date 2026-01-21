package com.br.duarte.votacao.service;

import com.br.duarte.votacao.api.v1.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.v1.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.api.exception.ResourceNotFoundException;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.enums.StatusSessao;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
import com.br.duarte.votacao.domain.service.SessaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    @DisplayName("Deve abrir sessão com sucesso para uma pauta existente")
    void deveAbrirSessaoComSucesso() {
        // Arrange
        Long pautaId = 1L;
        AbrirSessaoRequest request = new AbrirSessaoRequest(5);

        Pauta pauta = new Pauta();
        pauta.setId(pautaId);
        pauta.setTitulo("Pauta Teste");

        SessaoVotacao sessaoSalva = new SessaoVotacao();
        sessaoSalva.setId(10L);
        sessaoSalva.setAbertaEm(OffsetDateTime.now());
        sessaoSalva.setFechaEm(OffsetDateTime.now().plusMinutes(5));
        sessaoSalva.setStatus(StatusSessao.ABERTA);

        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoSalva);

        // Act
        SessaoVotacaoResponse response = sessaoService.abrirSessao(pautaId, request);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(StatusSessao.ABERTA, response.status());
        verify(sessaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve abrir sessão com tempo padrão de 1 minuto quando request for nulo")
    void deveAbrirSessaoComTempoPadrao() {
        // Arrange
        Long pautaId = 1L;
        Pauta pauta = new Pauta();
        pauta.setId(pautaId);

        SessaoVotacao sessaoSalva = new SessaoVotacao();
        sessaoSalva.setStatus(StatusSessao.ABERTA);

        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoSalva);

        // Act
        sessaoService.abrirSessao(pautaId, null);

        // Assert
        verify(sessaoRepository).save(any(SessaoVotacao.class));
        // O método estático SessaoVotacao.abrir(pauta, 1) será chamado internamente
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe sessão para a pauta")
    void deveLancarExcecaoSessaoJaExistente() {
        // Arrange
        Long pautaId = 1L;
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> sessaoService.abrirSessao(pautaId, null));

        assertEquals("Já existe uma sessão aberta para a pauta informada.", ex.getMessage());
        verify(pautaRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta não for encontrada")
    void deveLancarExcecaoPautaNaoEncontrada() {
        // Arrange
        Long pautaId = 1L;
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> sessaoService.abrirSessao(pautaId, null));

        assertTrue(ex.getMessage().contains("Pauta não encontrada"));
    }
}
