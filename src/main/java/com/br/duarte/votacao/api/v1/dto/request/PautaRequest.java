package com.br.duarte.votacao.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class PautaRequest {

    @Schema(description = "Título da pauta a ser votada", example = "Aumento do VR para 2026")
    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255)
    private String titulo;

    @Schema(description = "Descrição detalhada sobre o tema da pauta",
            example = "Votação para decidir o reajuste de 10% no vale refeição.")
    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 1000)
    private String descricao;

}
