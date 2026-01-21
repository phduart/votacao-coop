package com.br.duarte.votacao.api.dto.response;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;

import java.util.Map;

public record ResultadoVotacaoResponse(
        Long pautaId,
        Map<OpcaoVoto, Long> resultado
) {}

