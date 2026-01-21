package com.br.duarte.votacao.api.v1.dto.response;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;

public record VotoContagem(OpcaoVoto opcao, Long total) {}