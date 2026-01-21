package com.br.duarte.votacao.api.v2.dto.response;

import java.util.Map;

public class TelaInfoResponse {

    private String tipo = "INFO";
    private String titulo;
    private Map<String, Object> conteudo;

    public TelaInfoResponse(String titulo, Map<String, Object> conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }
}
