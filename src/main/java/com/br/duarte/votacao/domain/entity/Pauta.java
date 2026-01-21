package com.br.duarte.votacao.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "PAUTA")
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "CRIADA_EM", nullable = false, updatable = false)
    private OffsetDateTime criadaEm;

    @PrePersist
    void prePersist() {
        this.criadaEm = OffsetDateTime.now();
    }
}