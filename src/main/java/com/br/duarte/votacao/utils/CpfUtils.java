package com.br.duarte.votacao.utils;

import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.client.userInfo.UserInfoClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.beanvalidation.IntegrationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CpfUtils {

    private final UserInfoClient userInfoClient;

    /**
     * Valida a elegibilidade do CPF.
     * Se a API estiver indisponível ou CPF não existir, aplica regra de contingência
     * baseada na validade do CPF pelos dígitos verificadores.
     *
     * @param cpf CPF completo com 11 dígitos
     */
    @Cacheable(value = "cpf", key = "#cpf")
    public void validarElegibilidade(String cpf) {
        try {
            var response = userInfoClient.checkCpfStatus(cpf);
            if ("UNABLE_TO_VOTE".equals(response.status())) {
                throw new BusinessException("Associado não autorizado a votar.");
            }
        } catch (FeignException.NotFound e) {
            log.warn("API de CPF fora do ar ou CPF inexistente. Aplicando validação de dígito do CPF para: {}", cpf);
            aplicarRegraContingencia(cpf);
        } catch (FeignException e) {
            throw new IntegrationException("Falha na comunicação com validador de CPF.");
        }
    }

    /**
     * Valida os dígitos verificadores do CPF.
     *
     * @param cpf CPF completo com 11 dígitos
     */
    private void aplicarRegraContingencia(String cpf) {
        if (!isCpfValido(cpf)) {
            throw new BusinessException("CPF inválido pelo critério de dígito verificador.");
        }
    }

    /**
     * Valida se o CPF é válido pelos dígitos verificadores.
     *
     * @param cpf CPF com 11 dígitos
     * @return true se válido, false caso contrário
     */
    private boolean isCpfValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        int[] digitos = new int[11];
        for (int i = 0; i < 11; i++) {
            digitos[i] = Character.getNumericValue(cpf.charAt(i));
        }

        // Calcula primeiro dígito verificador
        int soma1 = 0;
        for (int i = 0, peso = 10; i < 9; i++, peso--) {
            soma1 += digitos[i] * peso;
        }
        int resto1 = soma1 % 11;
        int dv1 = (resto1 < 2) ? 0 : 11 - resto1;
        if (digitos[9] != dv1) {
            return false;
        }

        // Calcula segundo dígito verificador
        int soma2 = 0;
        for (int i = 0, peso = 11; i < 10; i++, peso--) {
            soma2 += digitos[i] * peso;
        }
        int resto2 = soma2 % 11;
        int dv2 = (resto2 < 2) ? 0 : 11 - resto2;
        return digitos[10] == dv2;
    }
}
