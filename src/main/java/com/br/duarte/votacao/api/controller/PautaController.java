package com.br.duarte.votacao.api.controller;

import com.br.duarte.votacao.api.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.service.VotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(name = "Pautas")
public class PautaController {

    private final PautaRepository pautaRepository;
    private final VotacaoService votacaoService;

    public PautaController(PautaRepository pautaRepository,
                           VotacaoService votacaoService) {
        this.pautaRepository = pautaRepository;
        this.votacaoService = votacaoService;
    }

    @Operation(summary = "Cadastrar nova pauta")
    @PostMapping
    public Pauta criar(@RequestBody Pauta pauta) {
        return pautaRepository.save(pauta);
    }

    @Operation(summary = "Obter resultado da votação da pauta")
    @GetMapping("/{pautaId}/resultado")
    public ResultadoVotacaoResponse resultado(@PathVariable Long pautaId) {
        return votacaoService.apurar(pautaId);
    }
}
