package com.br.duarte.votacao.domain.repository;

import com.br.duarte.votacao.domain.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<Pauta, Long> {
}

