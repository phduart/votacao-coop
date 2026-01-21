package com.br.duarte.votacao.api.dto.response;

import com.br.duarte.votacao.domain.enums.StatusSessao;

import java.time.OffsetDateTime;

public record SessaoVotacaoResponse(
        Long id,
        Long pautaId,
        OffsetDateTime abertaEm,
        OffsetDateTime fechaEm,
        StatusSessao status
) {}
