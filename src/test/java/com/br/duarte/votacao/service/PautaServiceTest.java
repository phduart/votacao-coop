package com.br.duarte.votacao.service;

import com.br.duarte.votacao.api.v1.dto.request.PautaRequest;
import com.br.duarte.votacao.api.v1.dto.response.PautaResponse;
import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.api.v1.mapper.PautaMapper;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.service.PautaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private PautaMapper pautaMapper;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Deve salvar uma pauta com sucesso usando setters")
    void deveSalvarPautaComSucesso() {
        // Arrange
        PautaRequest request =
                PautaRequest.builder()
                        .titulo("Aumento VR")
                        .descricao("Aumento de 10%")
                        .build();

        Pauta pautaEntity = new Pauta();
        pautaEntity.setTitulo(request.getTitulo());
        pautaEntity.setDescricao(request.getDescricao());

        Pauta pautaSalva = new Pauta();
        pautaSalva.setId(1L);
        pautaSalva.setTitulo(request.getTitulo());
        pautaSalva.setDescricao(request.getDescricao());

        PautaResponse response = new PautaResponse();
        response.setId(1L);
        response.setTitulo(pautaSalva.getTitulo());
        response.setDescricao(pautaSalva.getDescricao());

        when(pautaMapper.toEntity(any(PautaRequest.class))).thenReturn(pautaEntity);
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaSalva);
        when(pautaMapper.toResponse(any(Pauta.class))).thenReturn(response);

        // Act
        PautaResponse resultado = pautaService.salvar(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Aumento VR", resultado.getTitulo());
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve listar todas as pautas")
    void deveListarTodasAsPautas() {
        // Arrange
        Pauta pauta = new Pauta();
        pauta.setId(1L);

        PautaResponse response = new PautaResponse();
        response.setId(1L);

        when(pautaRepository.findAll()).thenReturn(List.of(pauta));
        when(pautaMapper.toResponse(pauta)).thenReturn(response);

        // Act
        List<PautaResponse> resultado = pautaService.listarTodas();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoBuscaIdInexistente() {
        // Arrange
        Long id = 99L;
        when(pautaRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> pautaService.buscarPorId(id));

        assertEquals("Pauta não encontrada", exception.getMessage());
    }
}