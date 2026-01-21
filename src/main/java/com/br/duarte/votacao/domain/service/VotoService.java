package com.br.duarte.votacao.domain.service;

import com.br.duarte.votacao.api.v1.dto.request.VotoRequest;
import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.entity.Voto;
import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
import com.br.duarte.votacao.domain.repository.VotoRepository;
import com.br.duarte.votacao.utils.CpfUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final SessaoService sessaoService;
    private final VotoRepository votoRepository;
    private final CpfUtils cpfUtils;

    public void votar(Long pautaId, VotoRequest request) {
        cpfUtils.validarElegibilidade(request.cpf());

        registrarVotoNoBanco(pautaId, request);
    }

    @Transactional
    protected void registrarVotoNoBanco(Long pautaId, VotoRequest request) {
        SessaoVotacao sessao = sessaoService.getSessaoPorPautaId(pautaId);

        validarSessaoAtiva(sessao);

        if (votoRepository.existsByPautaIdAndCpf(pautaId, request.cpf())) {
            throw new BusinessException("Associado já votou nesta pauta.");
        }

        Voto voto = Voto.builder()
                .pauta(sessao.getPauta())
                .cpf(request.cpf())
                .voto(request.voto())
                .build();

        votoRepository.save(voto);
    }

    private void validarSessaoAtiva(SessaoVotacao sessao) {
        if (!sessao.isAberta()) {
            throw new BusinessException("A sessão de votação já está encerrada.");
        }
    }
}