//package com.br.duarte.votacao.service;
//
//import com.br.duarte.votacao.api.dto.request.VotoRequest;
//import com.br.duarte.votacao.api.exception.BusinessException;
//import com.br.duarte.votacao.domain.entity.Pauta;
//import com.br.duarte.votacao.domain.entity.SessaoVotacao;
//import com.br.duarte.votacao.domain.entity.Voto;
//import com.br.duarte.votacao.domain.enums.OpcaoVoto;
//import com.br.duarte.votacao.domain.enums.StatusSessao;
//import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
//import com.br.duarte.votacao.domain.repository.VotoRepository;
//import com.br.duarte.votacao.domain.service.VotoService;
//import com.br.duarte.votacao.utils.CpfUtils;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.OffsetDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class VotoServiceTest {
//
//    @Mock
//    private SessaoVotacaoRepository sessaoRepository;
//
//    @Mock
//    private VotoRepository votoRepository;
//
//    @Mock
//    private CpfUtils cpfUtils; // O Mockito injetará este mock no VotoService
//
//    @InjectMocks
//    private VotoService votoService;
//
//    @Test
//    @DisplayName("Deve registrar um voto com sucesso")
//    void deveRegistrarVotoComSucesso() {
//        // Arrange
//        Long pautaId = 1L;
//        VotoRequest request = new VotoRequest("12345678901", OpcaoVoto.SIM);
//
//        Pauta pauta = new Pauta();
//        pauta.setId(pautaId);
//
//        // Usando o método estático para criar a sessão, já que o construtor é protected
//        SessaoVotacao sessao = SessaoVotacao.abrir(pauta, 10);
//
//        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
//        when(votoRepository.existsByPautaIdAndCpf(pautaId, request.cpf())).thenReturn(false);
//
//        // Act
//        assertDoesNotThrow(() -> votoService.votar(pautaId, request));
//
//        // Assert
//        verify(cpfUtils).validarElegibilidade(request.cpf());
//        verify(votoRepository).save(any(Voto.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando o associado já tiver votado")
//    void deveLancarExcecaoQuandoJaVotou() {
//        // Arrange
//        Long pautaId = 1L;
//        VotoRequest request = new VotoRequest("12345678901", OpcaoVoto.SIM);
//
//        Pauta pauta = new Pauta();
//        pauta.setId(pautaId);
//        SessaoVotacao sessao = SessaoVotacao.abrir(pauta, 10);
//
//        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
//        when(votoRepository.existsByPautaIdAndCpf(pautaId, request.cpf())).thenReturn(true);
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class,
//                () -> votoService.votar(pautaId, request));
//
//        assertEquals("Associado já votou nesta pauta.", exception.getMessage());
//        verify(votoRepository, never()).save(any(Voto.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando a sessão de votação estiver encerrada")
//    void deveLancarExcecaoSessaoEncerrada() {
//        Long pautaId = 1L;
//        VotoRequest request = new VotoRequest("12345678901", OpcaoVoto.SIM);
//
//        Pauta pauta = new Pauta();
//        pauta.setId(pautaId);
//
//        SessaoVotacao sessaoEncerrada = new SessaoVotacao();
//        sessaoEncerrada.setPauta(pauta);
//
//        sessaoEncerrada.setFechaEm(OffsetDateTime.now().minusHours(1));
//
//        sessaoEncerrada.setStatus(StatusSessao.FECHADA);
//
//        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessaoEncerrada));
//
//        BusinessException exception = assertThrows(BusinessException.class,
//                () -> votoService.votar(pautaId, request));
//
//        assertEquals("A sessão de votação já está encerrada.", exception.getMessage());
//    }
//}