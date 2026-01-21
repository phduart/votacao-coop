package com.br.duarte.votacao.api.v2.dto.request;

import java.util.Map;

public class FormularioRequest {

    private Map<String, Object> contexto;
    private Map<String, Object> dados;

    public Map<String, Object> getContexto() {
        return contexto;
    }

    public void setContexto(Map<String, Object> contexto) {
        this.contexto = contexto;
    }

    public Map<String, Object> getDados() {
        return dados;
    }

    public void setDados(Map<String, Object> dados) {
        this.dados = dados;
    }
}