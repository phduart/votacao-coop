package com.br.duarte.votacao.domain.repository;

import com.br.duarte.votacao.domain.entity.SessaoVotacao;
import com.br.duarte.votacao.domain.enums.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    Optional<SessaoVotacao> findByPautaId(Long pautaId);

    boolean existsByPautaId(Long pautaId);

    List<SessaoVotacao> findByStatusAndFechaEmBefore(
            StatusSessao status,
            OffsetDateTime dataHora
    );
}
