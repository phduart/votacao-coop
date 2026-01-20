-- PAUTAS
INSERT INTO PAUTA (TITULO, DESCRICAO)
VALUES ('Aprovação do orçamento 2026', 'Votação do orçamento anual da cooperativa');

INSERT INTO PAUTA (TITULO, DESCRICAO)
VALUES ('Novo estatuto social', 'Alterações no estatuto da cooperativa');

-- SESSÕES (abertas por 1 minuto com Timezone de Brasília/SP)
-- Utilizamos FROM_TZ para garantir o offset -03:00 ou -02:00 (ajustado pelo Oracle)
INSERT INTO SESSAO_VOTACAO (PAUTA_ID, ABERTA_EM, FECHA_EM, STATUS)
VALUES (
           1,
           FROM_TZ(CAST(CURRENT_TIMESTAMP AS TIMESTAMP), 'America/Sao_Paulo'),
           FROM_TZ(CAST(CURRENT_TIMESTAMP AS TIMESTAMP), 'America/Sao_Paulo') + INTERVAL '1' MINUTE,
           'ABERTA'
       );

INSERT INTO SESSAO_VOTACAO (PAUTA_ID, ABERTA_EM, FECHA_EM, STATUS)
VALUES (
           2,
           FROM_TZ(CAST(CURRENT_TIMESTAMP AS TIMESTAMP), 'America/Sao_Paulo'),
           FROM_TZ(CAST(CURRENT_TIMESTAMP AS TIMESTAMP), 'America/Sao_Paulo') + INTERVAL '1' MINUTE,
           'ABERTA'
       );

-- VOTOS
INSERT INTO VOTO (PAUTA_ID, CPF, VOTO)
VALUES (1, '12345678901', 'SIM');

INSERT INTO VOTO (PAUTA_ID, CPF, VOTO)
VALUES (1, '98765432100', 'NAO');

INSERT INTO VOTO (PAUTA_ID, CPF, VOTO)
VALUES (2, '12345678901', 'SIM');

COMMIT;