-- PAUTAS
INSERT INTO PAUTA (TITULO, DESCRICAO)
VALUES ('Aprovação do orçamento 2026', 'Votação do orçamento anual da cooperativa');

INSERT INTO PAUTA (TITULO, DESCRICAO)
VALUES ('Novo estatuto social', 'Alterações no estatuto da cooperativa');

-- SESSÕES (abertas por 1 minuto)
INSERT INTO SESSAO_VOTACAO (PAUTA_ID, ABERTA_EM, FECHA_EM, STATUS)
VALUES (
           1,
           SYSTIMESTAMP,
           SYSTIMESTAMP + INTERVAL '1' MINUTE,
           'ABERTA'
       );

INSERT INTO SESSAO_VOTACAO (PAUTA_ID, ABERTA_EM, FECHA_EM, STATUS)
VALUES (
           2,
           SYSTIMESTAMP,
           SYSTIMESTAMP + INTERVAL '1' MINUTE,
           'ABERTA'
       );

-- VOTOS (CPF como String)
INSERT INTO VOTO (PAUTA_ID, CPF, VOTO)
VALUES (1, '12345678901', 'SIM');

INSERT INTO VOTO (PAUTA_ID, CPF, VOTO)
VALUES (1, '98765432100', 'NAO');

INSERT INTO VOTO (PAUTA_ID, CPF, VOTO)
VALUES (2, '12345678901', 'SIM');
