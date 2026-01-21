package com.br.duarte.votacao.api.v1.controller;

import com.br.duarte.votacao.api.v1.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.v1.dto.response.SessaoVotacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sessões de Votação")
public interface ISessaoVotacaoController {

    @Operation(summary = "Abrir sessão de votação", description = "Inicia o período de votação para uma pauta específica.")
    @ApiResponse(responseCode = "200", description = "Sessão aberta com sucesso")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    @ApiResponse(responseCode = "400", description = "Sessão já existente ou dados inválidos")
    SessaoVotacaoResponse abrirSessao(
            @Parameter(description = "ID da pauta", example = "1") Long pautaId,
            AbrirSessaoRequest request);
}
