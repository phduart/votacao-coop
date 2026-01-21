package com.br.duarte.votacao.api.v2.strategy;

import com.br.duarte.votacao.api.v1.dto.request.PautaRequest;
import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;
import com.br.duarte.votacao.domain.service.PautaService;
import com.br.duarte.votacao.utils.ValidacaoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarPautaHandler implements FormularioHandler {
    private final PautaService pautaService;
    private final ValidacaoUtils utils;

    @Override
    public boolean supports(String acao) {
        return "criar-pauta".equalsIgnoreCase(acao);
    }

    @Override
    public Object handle(FormularioRequest request) {
        String titulo = utils.getString(request.getDados(), "titulo");
        pautaService.salvar(PautaRequest.builder().titulo(titulo).build());
        return null;
    }
}
