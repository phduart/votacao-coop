package com.br.duarte.votacao.api.v2.strategy;

import com.br.duarte.votacao.api.v1.dto.request.VotoRequest;
import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.service.VotoService;
import com.br.duarte.votacao.utils.ValidacaoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VotarHandler implements FormularioHandler {

    private final VotoService votoService;
    private final ValidacaoUtils utils;

    @Override
    public boolean supports(String acao) {
        return "votar".equalsIgnoreCase(acao);
    }

    @Override
    public Object handle(FormularioRequest request) {
        Long pautaId = utils.getLong(request.getContexto(), "pautaId");
        String cpf = utils.getString(request.getDados(), "cpf");
        String votoString = utils.getString(request.getDados(), "voto");

        VotoRequest votoRequest = VotoRequest.builder()
                .cpf(cpf)
                .voto(OpcaoVoto.valueOf(votoString.toUpperCase()))
                .build();

        votoService.votar(pautaId, votoRequest);
        return null;
    }
}
