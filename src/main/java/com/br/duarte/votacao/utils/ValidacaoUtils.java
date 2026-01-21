package com.br.duarte.votacao.utils;

import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidacaoUtils {

    public void validarRequest(FormularioRequest request) {
        if (request.getContexto() == null || request.getContexto().isEmpty()) {
            throw new IllegalArgumentException("Contexto é obrigatório");
        }
        if (request.getDados() == null || request.getDados().isEmpty()) {
            throw new IllegalArgumentException("Dados do formulário são obrigatórios");
        }
    }

    public String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Campo obrigatório ausente: " + key);
        }
        return value.toString();
    }

    public Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Campo obrigatório ausente: " + key);
        }
        return Long.valueOf(value.toString());
    }

    public Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Campo obrigatório ausente: " + key);
        }
        return Integer.valueOf(value.toString());
    }
}
