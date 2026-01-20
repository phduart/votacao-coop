package com.br.duarte.votacao.service;

import com.br.duarte.votacao.api.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.dto.request.VotoRequest;
import com.br.duarte.votacao.api.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.api.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.entity.Voto;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.enums.StatusSessao;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
import com.br.duarte.votacao.domain.repository.VotoRepository;
import com.br.duarte.votacao.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class VotacaoService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;

    public VotacaoService(PautaRepository pautaRepository,
                          SessaoVotacaoRepository sessaoRepository,
                          VotoRepository votoRepository) {
        this.pautaRepository = pautaRepository;
        this.sessaoRepository = sessaoRepository;
        this.votoRepository = votoRepository;
    }

    // ----------------------------
    // Abrir sessão
    // ----------------------------
    @Transactional
    public SessaoVotacaoResponse abrirSessao(Long pautaId, AbrirSessaoRequest request) {

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new BusinessException("Pauta não encontrada"));

        if (sessaoRepository.findByPautaId(pautaId).isPresent()) {
            throw new BusinessException("Já existe sessão para esta pauta");
        }

        int duracao = (request == null || request.minutos() == null || request.minutos() <= 0)
                ? 1
                : request.minutos();

        OffsetDateTime agora = OffsetDateTime.now();

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setAbertaEm(agora);
        sessao.setFechaEm(agora.plusMinutes(duracao));
        sessao.setStatus(StatusSessao.ABERTA);

        SessaoVotacao salva = sessaoRepository.save(sessao);

        return toResponse(salva);
    }

    // ----------------------------
    // Registrar voto
    // ----------------------------
    @Transactional
    public void votar(Long pautaId, VotoRequest request) {

        SessaoVotacao sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new BusinessException("Sessão não encontrada"));

        validarSessao(sessao);

        if (votoRepository.existsByPautaIdAndCpf(pautaId, request.cpf())) {
            throw new BusinessException("Associado já votou nesta pauta");
        }

        Voto voto = new Voto();
        voto.setPauta(sessao.getPauta());
        voto.setCpf(request.cpf());
        voto.setVoto(request.voto());

        votoRepository.save(voto);
    }

    // ----------------------------
    // Apuração
    // ----------------------------
    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse apurar(Long pautaId) {

        List<Object[]> dados = votoRepository.contarVotosPorPauta(pautaId);

        Map<OpcaoVoto, Long> resultado = new EnumMap<>(OpcaoVoto.class);
        resultado.put(OpcaoVoto.SIM, 0L);
        resultado.put(OpcaoVoto.NAO, 0L);

        for (Object[] linha : dados) {
            OpcaoVoto voto = (OpcaoVoto) linha[0];
            Long total = (Long) linha[1];
            resultado.put(voto, total);
        }

        return new ResultadoVotacaoResponse(pautaId, resultado);
    }

    // ----------------------------
    // Regras privadas
    // ----------------------------
    private void validarSessao(SessaoVotacao sessao) {

        if (sessao.getStatus() == StatusSessao.FECHADA) {
            throw new BusinessException("Sessão encerrada");
        }

        if (OffsetDateTime.now().isAfter(sessao.getFechaEm())) {
            sessao.setStatus(StatusSessao.FECHADA);
            sessaoRepository.save(sessao);
            throw new BusinessException("Sessão expirada");
        }
    }

    private SessaoVotacaoResponse toResponse(SessaoVotacao sessao) {
        return new SessaoVotacaoResponse(
                sessao.getId(),
                sessao.getPauta().getId(),
                sessao.getAbertaEm(),
                sessao.getFechaEm(),
                sessao.getStatus()
        );
    }
}

