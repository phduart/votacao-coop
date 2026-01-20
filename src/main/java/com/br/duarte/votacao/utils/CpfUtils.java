package com.br.duarte.votacao.utils;

import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.client.userInfo.UserInfoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.beanvalidation.IntegrationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CpfUtils {

    private final UserInfoClient userInfoClient;

    public void validarElegibilidade(String cpf) {
        try {
            var response = userInfoClient.checkCpfStatus(cpf);
            if ("UNABLE_TO_VOTE".equals(response.status())) {
                throw new BusinessException("Associado não autorizado a votar.");
            }
        } catch (feign.FeignException.NotFound e) {
            log.warn("API de CPF fora do ar ou CPF inexistente. Aplicando regra de contingência para: {}", cpf);
            aplicarRegraContingencia(cpf);
        } catch (feign.FeignException e) {
            throw new IntegrationException("Falha na comunicação com validador de CPF.");
        }
    }

    private void aplicarRegraContingencia(String cpf) {
        char ultimoDigito = cpf.charAt(cpf.length() - 1);
        if (Character.getNumericValue(ultimoDigito) % 2 != 0) {
            throw new BusinessException("CPF não elegível pelo critério de contingência.");
        }
    }
}
