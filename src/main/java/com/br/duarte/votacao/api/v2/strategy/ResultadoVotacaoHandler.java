package com.br.duarte.votacao.api.v2.strategy;

import com.br.duarte.votacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.api.v2.dto.request.FormularioRequest;
import com.br.duarte.votacao.api.v2.dto.response.TelaInfoResponse;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.service.VotacaoService;
import com.br.duarte.votacao.utils.ValidacaoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ResultadoVotacaoHandler implements FormularioHandler {

    private final VotacaoService votacaoService;
    private final ValidacaoUtils utils;

    @Override
    public boolean supports(String acao) {
        return "resultado-votacao".equalsIgnoreCase(acao);
    }

    @Override
    public Object handle(FormularioRequest request) {
        Long pautaId = utils.getLong(request.getContexto(), "pautaId");
        ResultadoVotacaoResponse resultado = votacaoService.apurar(pautaId);

        return montarTelaInfo(pautaId, resultado);
    }

    private TelaInfoResponse montarTelaInfo(Long pautaId, ResultadoVotacaoResponse resultado) {
        long sim = resultado.resultado().getOrDefault(OpcaoVoto.SIM, 0L);
        long nao = resultado.resultado().getOrDefault(OpcaoVoto.NAO, 0L);

        String vencedor = (sim > nao) ? "SIM" : (nao > sim) ? "NAO" : "EMPATE";

        Map<String, Object> conteudo = Map.of(
                "pautaId", pautaId,
                "votosSim", sim,
                "votosNao", nao,
                "vencedor", vencedor
        );

        return new TelaInfoResponse("Resultado Final", conteudo);
    }
}