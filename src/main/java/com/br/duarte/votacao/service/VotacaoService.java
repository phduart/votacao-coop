package com.br.duarte.votacao.service;

import com.br.duarte.votacao.api.dto.request.AbrirSessaoRequest;
import com.br.duarte.votacao.api.dto.request.VotoRequest;
import com.br.duarte.votacao.api.dto.response.ResultadoVotacaoResponse;
import com.br.duarte.votacao.api.dto.response.SessaoVotacaoResponse;
import com.br.duarte.votacao.client.userInfo.UserInfoClient;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.entity.Voto;
import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import com.br.duarte.votacao.domain.enums.StatusSessao;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import com.br.duarte.votacao.domain.repository.SessaoVotacaoRepository;
import com.br.duarte.votacao.domain.repository.VotoRepository;
import com.br.duarte.votacao.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotacaoService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;
    private final UserInfoClient userInfoClient;

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

        try {
            var response = userInfoClient.checkCpfStatus(request.cpf());

            if ("UNABLE_TO_VOTE".equals(response.status())) {
                throw new BusinessException("Associado não autorizado a votar.");
            }

        } catch (feign.FeignException.NotFound e) {
            log.info(" [ API CPF ] - Feing Not Found | Seguindo fluxo alternativo para Validar CPF ");
            if (!isUltimoDigitoPar(request.cpf())) {
                throw new BusinessException("CPF não encontrado e não elegível pelo critério de contingência.");
            }
        } catch (feign.FeignException e) {
            throw new BusinessException("Erro na integração com o sistema de validação de CPF.");
        }

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

    @Transactional
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

    private SessaoVotacaoResponse toResponse(SessaoVotacao sessao) {
        return new SessaoVotacaoResponse(
                sessao.getId(),
                sessao.getPauta().getId(),
                sessao.getAbertaEm(),
                sessao.getFechaEm(),
                sessao.getStatus()
        );
    }

    /**
     * Verifica se o último dígito do CPF é par.
     */
    private boolean isUltimoDigitoPar(String cpf) {
        if (cpf == null || cpf.isEmpty()) return false;

        // Remove caracteres não numéricos se houver
        String apenasNumeros = cpf.replaceAll("\\D", "");
        char ultimoChar = apenasNumeros.charAt(apenasNumeros.length() - 1);
        int ultimoDigito = Character.getNumericValue(ultimoChar);

        return ultimoDigito % 2 == 0;
    }
}

