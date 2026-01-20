package com.br.duarte.votacao.api.dto.response;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;

public record VotoContagem(OpcaoVoto opcao, Long total) {}