package com.br.duarte.votacao.api.v1.controller.impl;

import com.br.duarte.votacao.api.v1.controller.ISessaoVotacaoController;
import com.br.duarte.votacao.api.v1.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.v1.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.domain.service.SessaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoVotacaoController implements ISessaoVotacaoController {

    private final SessaoService sessaoService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoVotacaoResponse abrirSessao(
            @PathVariable Long pautaId,
            @RequestBody(required = false) @Valid AbrirSessaoRequest request) {

        return sessaoService.abrirSessao(pautaId, request);
    }
}

