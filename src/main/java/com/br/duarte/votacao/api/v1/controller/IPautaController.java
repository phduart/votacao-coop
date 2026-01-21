package com.br.duarte.votacao.api.v1.controller;

import com.br.duarte.votacao.api.v1.dto.request.PautaRequest;
import com.br.duarte.votacao.api.v1.dto.response.PautaResponse;
import com.br.duarte.votacao.api.v1.dto.response.ResultadoVotacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Pautas", description = "Endpoints para gerenciamento de pautas e apuração de resultados")
public interface IPautaController {

    @Operation(summary = "Cadastrar nova pauta", description = "Cria uma nova pauta para votação no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content)
    })
    PautaResponse criar(PautaRequest pauta);

    @Operation(summary = "Obter resultado da votação", description = "Retorna a contagem de votos e o resultado final de uma pauta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado obtido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content)
    })
    ResultadoVotacaoResponse resultado(
            @Parameter(description = "ID da pauta", example = "1") Long pautaId
    );
}
