package com.br.duarte.votacao.api.v2.controller;

import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;
import com.br.duarte.votacao.api.v2.dto.response.RespostaApi;
import com.br.duarte.votacao.api.v2.strategy.FormularioHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/formularios")
@RequiredArgsConstructor
public class FormularioV2Controller {

    private final List<FormularioHandler> handlers;

    @PostMapping("/{acao}")
    public ResponseEntity<RespostaApi<Object>> processar(@PathVariable String acao, @RequestBody FormularioRequest request) {
        return handlers.stream()
                .filter(h -> h.supports(acao))
                .findFirst()
                .map(h -> ResponseEntity.ok(RespostaApi.sucesso("Sucesso", h.handle(request))))
                .orElseThrow(() -> new BusinessException(acao));
    }
}