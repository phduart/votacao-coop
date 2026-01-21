package com.br.duarte.votacao.domain.entity;

import com.br.duarte.votacao.domain.enums.StatusSessao;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = "SESSAO_VOTACAO")
public class SessaoVotacao implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public static SessaoVotacao abrir(Pauta pauta, Integer minutos) {
        SessaoVotacao sessao = new SessaoVotacao();
        sessao.pauta = pauta;
        sessao.abertaEm = OffsetDateTime.now();

        int duracaoEfetiva = (minutos == null || minutos <= 0) ? 1 : minutos;
        sessao.fechaEm = sessao.abertaEm.plusMinutes(duracaoEfetiva);

        sessao.status = StatusSessao.ABERTA;
        return sessao;
    }

    /**
     * Verifica se a sessão é válida para votação.
     * Uma sessão está aberta se o status for ABERTA e o horário atual
     * estiver entre o início e o fim.
     */
    public boolean isAberta() {
        OffsetDateTime agora = OffsetDateTime.now();
        return StatusSessao.ABERTA.equals(this.status)
                && agora.isAfter(this.abertaEm)
                && agora.isBefore(this.fechaEm);
    }
}
