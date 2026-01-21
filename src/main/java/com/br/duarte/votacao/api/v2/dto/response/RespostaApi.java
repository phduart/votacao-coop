package com.br.duarte.votacao.api.v2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RespostaApi<T> {
    private final boolean sucesso;
    private final String mensagem;
    private final T dados; // Campo genérico para qualquer retorno

    public static <T> RespostaApi<T> sucesso(String mensagem, T dados) {
        return new RespostaApi<>(true, mensagem, dados);
    }

    public static <T> RespostaApi<T> sucesso(String mensagem) {
        return new RespostaApi<>(true, mensagem, null);
    }

    public static <T> RespostaApi<T> erro(String mensagem) {
        return new RespostaApi<>(false, mensagem, null);
    }
}