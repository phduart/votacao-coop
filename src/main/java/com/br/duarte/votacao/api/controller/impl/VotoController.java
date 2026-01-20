package com.br.duarte.votacao.api.controller.impl;

import com.br.duarte.votacao.api.controller.IVotoController;
import com.br.duarte.votacao.api.dto.request.VotoRequest;
import com.br.duarte.votacao.domain.service.VotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController implements IVotoController {

    private final VotoService service;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void votar(@PathVariable Long pautaId, @RequestBody @Valid VotoRequest request) {
        service.votar(pautaId, request);
    }
}

