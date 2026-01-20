package com.br.duarte.votacao.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PautaResponse {

    @Schema(description = "Identificador único da pauta", example = "1")
    private Long id;

    @Schema(description = "Título da pauta", example = "Aumento do VR para 2026")
    private String titulo;

    @Schema(description = "Descrição da pauta", example = "Votação para decidir o reajuste de 10% no vale refeição.")
    private String descricao;

    @Schema(description = "Data e hora da criação da pauta")
    private OffsetDateTime criadaEm;
}
