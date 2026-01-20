package com.br.duarte.votacao.api.controller;

import com.br.duarte.votacao.api.dto.request.VotoRequest;
import com.br.duarte.votacao.domain.service.VotacaoService;
import com.br.duarte.votacao.domain.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Votos")
@RequiredArgsConstructor
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController {

    private final VotoService service;

    @Operation(summary = "Registrar voto na pauta")
    @PostMapping
    public void votar(
            @PathVariable Long pautaId,
            @RequestBody VotoRequest request) {

        service.votar(pautaId, request);
    }
}

