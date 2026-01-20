package com.br.duarte.votacao.api.controller;

import com.br.duarte.votacao.api.dto.request.VotoRequest;
import com.br.duarte.votacao.service.VotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
@Tag(name = "Votos")
public class VotoController {

    private final VotacaoService votacaoService;

    public VotoController(VotacaoService votacaoService) {
        this.votacaoService = votacaoService;
    }

    @Operation(summary = "Registrar voto na pauta")
    @PostMapping
    public void votar(
            @PathVariable Long pautaId,
            @RequestBody VotoRequest request) {

        votacaoService.votar(pautaId, request);
    }
}

