package com.br.duarte.votacao.api.dto.request;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados para registro de voto")
public record VotoRequest(

        @Schema(description = "CPF do associado (apenas números)", example = "12345678901")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(description = "Opção de voto escolhida", example = "SIM")
        @NotNull(message = "O voto (SIM/NAO) é obrigatório")
        OpcaoVoto voto
) {}