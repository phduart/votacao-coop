package com.br.duarte.votacao.domain.entity;

import com.br.duarte.votacao.domain.enums.OpcaoVoto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(
        name = "VOTO",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"PAUTA_ID", "CPF"})
        }
)
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PAUTA_ID")
    private Pauta pauta;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private OpcaoVoto voto;

    @Column(name = "VOTADO_EM", nullable = false, updatable = false)
    private OffsetDateTime votadoEm;

    @PrePersist
    void prePersist() {
        this.votadoEm = OffsetDateTime.now();
    }
}
