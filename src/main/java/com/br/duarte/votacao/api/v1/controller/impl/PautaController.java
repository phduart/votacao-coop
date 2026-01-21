package com.br.duarte.votacao.api.v1.controller.impl;

import com.br.duarte.votacao.api.v1.controller.IPautaController;
import com.br.duarte.votacao.api.v1.dto.request.PautaRequest;
import com.br.duarte.votacao.api.v1.dto.response.PautaResponse;
import com.br.duarte.votacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.domain.service.PautaService;
import com.br.duarte.votacao.domain.service.VotacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
public class PautaController implements IPautaController {

    private final PautaService pautaService;
    private final VotacaoService votacaoService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PautaResponse criar(@RequestBody @Valid PautaRequest pauta) {
        return pautaService.salvar(pauta);
    }

    @Override
    @GetMapping("/{pautaId}/resultado")
    public ResultadoVotacaoResponse resultado(@PathVariable Long pautaId) {
        return votacaoService.apurar(pautaId);
    }
}
