package com.br.duarte.votacao.api.v1.controller;

import com.br.duarte.votacao.api.v1.dto.request.VotoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Votos", description = "Endpoints para registro de votos")
public interface IVotoController {

    @Operation(summary = "Registrar voto na pauta",
            description = "Registra o voto (SIM/NAO) de um associado. Valida se a sessão está aberta e se o CPF já votou.")
    @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Voto duplicado ou sessão encerrada", content = @Content)
    @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada", content = @Content)
    void votar(
            @Parameter(description = "ID da pauta", example = "1") Long pautaId,
            VotoRequest request);
}
