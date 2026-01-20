package com.br.duarte.votacao.utils;

import org.springframework.stereotype.Component;

@Component
public class NumberUtils {

    /**
     * Verifica se o último dígito do CPF é par.
     */
    public boolean isUltimoDigitoPar(String cpf) {
        if (cpf == null || cpf.isEmpty()) return false;

        // Remove caracteres não numéricos se houver
        String apenasNumeros = cpf.replaceAll("\\D", "");
        char ultimoChar = apenasNumeros.charAt(apenasNumeros.length() - 1);
        int ultimoDigito = Character.getNumericValue(ultimoChar);

        return ultimoDigito % 2 == 0;
    }
}
