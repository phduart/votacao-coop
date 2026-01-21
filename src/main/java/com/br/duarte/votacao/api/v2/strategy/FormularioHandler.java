package com.br.duarte.votacao.api.v2.strategy;

import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;

public interface FormularioHandler {
    boolean supports(String acao);
    Object handle(FormularioRequest request);
}
