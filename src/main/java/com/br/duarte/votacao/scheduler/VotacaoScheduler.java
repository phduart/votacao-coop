package com.br.duarte.votacao.scheduler;

import com.br.duarte.votacao.domain.service.SessaoService;
import com.br.duarte.votacao.domain.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VotacaoScheduler {

    private final SessaoService service;

    @Scheduled(fixedDelayString = "${scheduler.sessao.delay}")
    public void encerrarSessoesAutomaticamente() {
        log.info("[ VotacaoScheduler ] -  Verificando Sessões Expiradas. ");
        service.fecharSessoesExpiradas();
    }
}

