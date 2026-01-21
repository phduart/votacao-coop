package com.br.duarte.votacao.api.v1.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dados para abertura de sessão de votação")
public record AbrirSessaoRequest(
        @Schema(description = "Tempo de duração da sessão em minutos. Se nulo, o padrão é 1 minuto.", example = "5")
        @Positive(message = "O tempo de duração deve ser um valor positivo")
        Integer minutos
) {}