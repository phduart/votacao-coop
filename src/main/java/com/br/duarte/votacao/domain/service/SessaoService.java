package com.br.duarte.votacao.domain.service;

import com.br.duarte.votacao.api.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.api.exception.ResourceNotFoundException;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.enums.StatusSessao;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    @Transactional
    public SessaoVotacaoResponse abrirSessao(Long pautaId, AbrirSessaoRequest request) {
        validarSessaoInexistente(pautaId);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada com ID: " + pautaId));

        Integer minutos = Optional.ofNullable(request).map(AbrirSessaoRequest::minutos).orElse(1);
        SessaoVotacao novaSessao = SessaoVotacao.abrir(pauta, minutos);

        SessaoVotacao salva = sessaoRepository.save(novaSessao);

        return new SessaoVotacaoResponse(
                salva.getId(),
                pautaId,
                salva.getAbertaEm(),
                salva.getFechaEm(),
                salva.getStatus()
        );
    }

    private void validarSessaoInexistente(Long pautaId) {
        if (sessaoRepository.existsByPautaId(pautaId)) {
            throw new BusinessException("Já existe uma sessão aberta para a pauta informada.");
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void fecharSessoesExpiradas() {

        OffsetDateTime agora = OffsetDateTime.now();

        List<SessaoVotacao> sessoes =
                sessaoRepository.findByStatusAndFechaEmBefore(
                        StatusSessao.ABERTA,
                        agora
                );

        sessoes.forEach(sessao ->
                sessao.setStatus(StatusSessao.FECHADA)
        );
    }
}
