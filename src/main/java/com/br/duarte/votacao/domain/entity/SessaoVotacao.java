package com.br.duarte.votacao.domain.entity;

import com.br.duarte.votacao.domain.enums.StatusSessao;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "SESSAO_VOTACAO")
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAUTA_ID")
    private Pauta pauta;

    @Column(name = "ABERTA_EM", nullable = false)
    private OffsetDateTime abertaEm;

    @Column(name = "FECHA_EM", nullable = false)
    private OffsetDateTime fechaEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSessao status;

    @PrePersist
    void prePersist() {
        this.abertaEm = OffsetDateTime.now();
        this.status = StatusSessao.ABERTA;
    }
}
