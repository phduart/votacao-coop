package com.br.duarte.votacao.domain.repository;

import com.br.duarte.votacao.api.v1.dto.response.VotoContagem;
import com.br.duarte.votacao.domain.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndCpf(Long pautaId, String cpf);

    @Query("""
        select new com.br.duarte.votacao.api.v1.dto.response.VotoContagem(
            v.voto,
            count(v.id)
        )
        from Voto v
        where v.pauta.id = :pautaId
        group by v.voto
    """)
    List<VotoContagem> contarVotosAgrupados(@Param("pautaId") Long pautaId);
}
