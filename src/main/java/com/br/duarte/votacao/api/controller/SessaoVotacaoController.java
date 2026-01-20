package com.br.duarte.votacao.api.controller;

import com.br.duarte.votacao.api.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.domain.service.SessaoService;
import com.br.duarte.votacao.domain.service.VotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Sessões de Votação")
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoVotacaoController {

    private final SessaoService service;

    @Operation(summary = "Abrir sessão de votação")
    @PostMapping
    public SessaoVotacaoResponse abrirSessao(
            @PathVariable Long pautaId,
            @RequestBody(required = false) AbrirSessaoRequest request) {

        return service.abrirSessao(pautaId, request);
    }
}

