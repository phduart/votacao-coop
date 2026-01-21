package com.br.duarte.votacao.domain.service;

import com.br.duarte.votacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.api.v1.dto.response.VotoContagem;
import com.br.duarte.votacao.api.exception.ResourceNotFoundException;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotacaoService {

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;

    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse apurar(Long pautaId) {
        if (!pautaRepository.existsById(pautaId)) {
            throw new ResourceNotFoundException("Pauta não encontrada com ID: " + pautaId);
        }

        List<VotoContagem> contagens = votoRepository.contarVotosAgrupados(pautaId);

        Map<OpcaoVoto, Long> votosNoBanco = contagens.stream()
                .collect(Collectors.toMap(VotoContagem::opcao, VotoContagem::total));

        Map<OpcaoVoto, Long> resultadoFinal = new EnumMap<>(OpcaoVoto.class);

        for (OpcaoVoto opcao : OpcaoVoto.values()) {
            resultadoFinal.put(opcao, votosNoBanco.getOrDefault(opcao, 0L));
        }

        return new ResultadoVotacaoResponse(pautaId, resultadoFinal);
    }

}

