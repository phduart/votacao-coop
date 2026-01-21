package com.br.duarte.votacao.api.v1.dto.response;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotoContagem {

    private final OpcaoVoto opcao;
    private final Long total;

    public VotoContagem(OpcaoVoto opcao, Long total) {
        this.opcao = opcao;
        this.total = total;
    }
}