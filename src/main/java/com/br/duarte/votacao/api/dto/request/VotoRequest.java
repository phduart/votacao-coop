package com.br.duarte.votacao.api.dto.request;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;

public record VotoRequest(
        String cpf,
        OpcaoVoto voto
) {}