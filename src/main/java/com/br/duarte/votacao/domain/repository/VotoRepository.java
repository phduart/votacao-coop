package com.br.duarte.votacao.domain.repository;

import com.br.duarte.votacao.api.v1.dto.response.VotoContagem;
import com.br.duarte.votacao.domain.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndCpf(Long pautaId, String cpf);

    @Query("SELECT new com.br.duarte.votacao.api.dto.response.VotoContagem(v.voto, COUNT(v)) " +
            "FROM Voto v " +
            "WHERE v.pauta.id = :pautaId " +
            "GROUP BY v.voto")
    List<VotoContagem> contarVotosAgrupados(Long pautaId);
}
