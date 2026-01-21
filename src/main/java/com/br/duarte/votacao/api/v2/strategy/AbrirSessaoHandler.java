package com.br.duarte.votacao.api.v2.strategy;

import com.br.duarte.votacao.api.v1.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;
import com.br.duarte.votacao.domain.service.SessaoService;
import com.br.duarte.votacao.utils.ValidacaoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AbrirSessaoHandler implements FormularioHandler {

    private final SessaoService sessaoService;
    private final ValidacaoUtils utils;

    @Override
    public boolean supports(String acao) {
        return "abrir-sessao".equalsIgnoreCase(acao);
    }

    @Override
    public Object handle(FormularioRequest request) {
        Long pautaId = utils.getLong(request.getContexto(), "pautaId");

        Integer duracao = null;
        if (request.getDados().containsKey("duracaoMinutos")) {
            duracao = utils.getInteger(request.getDados(), "duracaoMinutos");
        }

        AbrirSessaoRequest abrirSessaoRequest = new AbrirSessaoRequest(duracao);
        sessaoService.abrirSessao(pautaId, abrirSessaoRequest);
        return null;
    }
}
