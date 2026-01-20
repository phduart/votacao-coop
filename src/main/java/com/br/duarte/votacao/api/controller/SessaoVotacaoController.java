package com.br.duarte.votacao.api.controller;

import com.br.duarte.votacao.api.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.service.VotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
@Tag(name = "Sessões de Votação")
public class SessaoVotacaoController {

    private final VotacaoService votacaoService;

    public SessaoVotacaoController(VotacaoService votacaoService) {
        this.votacaoService = votacaoService;
    }

    @Operation(summary = "Abrir sessão de votação")
    @PostMapping
    public SessaoVotacaoResponse abrirSessao(
            @PathVariable Long pautaId,
            @RequestBody(required = false) AbrirSessaoRequest request) {

        return votacaoService.abrirSessao(pautaId, request);
    }
}

