import streamlit as st
import requests
import pandas as pd
import oracledb

# ==================================================
# CONFIG GERAL
# ==================================================
st.set_page_config(
    page_title="Sistema de Votação Cooperativa",
    layout="wide"
)

BASE_URL = "http://localhost:8080"

# ==================================================
# HELPERS API
# ==================================================
def post(url, payload):
    r = requests.post(url, json=payload)
    return r.status_code, r.json() if r.content else None


def get(url):
    r = requests.get(url)
    return r.status_code, r.json() if r.content else None


# ==================================================
# DB CONNECTION (ORACLE)
# ==================================================
@st.cache_resource
def get_connection():
    return oracledb.connect(
        user="votacao",
        password="votacao",
        dsn="localhost:1521/XEPDB1"
    )


@st.cache_data(ttl=60)
def load_data(query):
    conn = get_connection()
    return pd.read_sql(query, conn)


# ==================================================
# QUERIES
# ==================================================
QUERY_PAUTAS = """
SELECT ID, TITULO, DESCRICAO, CRIADA_EM
FROM PAUTA
ORDER BY CRIADA_EM DESC
"""

QUERY_SESSOES = """
SELECT s.ID, s.PAUTA_ID, p.TITULO, s.ABERTA_EM, s.FECHA_EM, s.STATUS
FROM SESSAO_VOTACAO s
JOIN PAUTA p ON p.ID = s.PAUTA_ID
ORDER BY s.ABERTA_EM DESC
"""

QUERY_VOTOS = """
SELECT v.PAUTA_ID, p.TITULO, v.VOTO, COUNT(*) TOTAL
FROM VOTO v
JOIN PAUTA p ON p.ID = v.PAUTA_ID
GROUP BY v.PAUTA_ID, p.TITULO, v.VOTO
"""

QUERY_VOTOS_RAW = """
SELECT p.TITULO, v.CPF, v.VOTO, v.VOTADO_EM
FROM VOTO v
JOIN PAUTA p ON p.ID = v.PAUTA_ID
ORDER BY v.VOTADO_EM DESC
"""

# ==================================================
# TABS
# ==================================================
tab_operacional, tab_dashboard = st.tabs([
    "🛠️ Operacional (API)",
    "📊 Dashboard / Análises"
])

# ==================================================
# TAB 1 – OPERACIONAL
# ==================================================
with tab_operacional:
    st.header("🛠️ Operações Diretas da API")

    col1, col2, col3, col4 = st.columns(4)

    # ------------------------------------------------
    # CRIAR PAUTA
    # ------------------------------------------------
    with col1:
        st.subheader("📌 Criar Pauta")

        titulo = st.text_input("Título")
        descricao = st.text_area("Descrição")

        if st.button("Criar Pauta", use_container_width=True):
            status, resp = post(
                f"{BASE_URL}/api/v1/pautas",
                {"titulo": titulo, "descricao": descricao}
            )

            st.json(resp)

    # ------------------------------------------------
    # ABRIR SESSÃO
    # ------------------------------------------------
    with col2:
        st.subheader("🕒 Abrir Sessão")

        pauta_id = st.number_input("ID da Pauta", min_value=1, step=1)
        minutos = st.number_input("Duração (min)", min_value=1, value=5)

        if st.button("Abrir Sessão", use_container_width=True):
            status, resp = post(
                f"{BASE_URL}/api/v1/pautas/{pauta_id}/sessao",
                {"minutos": minutos}
            )

            st.json(resp)

    # ------------------------------------------------
    # REGISTRAR VOTO
    # ------------------------------------------------
    with col3:
        st.subheader("✍️ Registrar Voto")

        pauta_id_voto = st.number_input("Pauta", min_value=1, step=1, key="pauta_voto")
        cpf = st.text_input("CPF")
        voto = st.selectbox("Voto", ["SIM", "NAO"])

        if st.button("Registrar Voto", use_container_width=True):
            status, resp = post(
                f"{BASE_URL}/api/v1/pautas/{pauta_id_voto}/votos",
                {"cpf": cpf, "voto": voto}
            )

            st.json(resp)

    # ------------------------------------------------
    # RESULTADO
    # ------------------------------------------------
    with col4:
        st.subheader("📊 Resultado da Pauta")

        pauta_resultado = st.number_input(
            "Pauta",
            min_value=1,
            step=1,
            key="resultado"
        )

        if st.button("Buscar Resultado", use_container_width=True):
            status, resp = get(
                f"{BASE_URL}/api/v1/pautas/{pauta_resultado}/resultado"
            )

            st.json(resp)

# ==================================================
# TAB 2 – DASHBOARD
# ==================================================
with tab_dashboard:
    st.header("📊 Dashboard – Votação Cooperativa")

    col_refresh, col_space = st.columns([1, 9])

    with col_refresh:
        if st.button("🔄 Atualizar Dados"):
            st.cache_data.clear()
            st.success("Dados atualizados")

    pautas_df = load_data(QUERY_PAUTAS)
    sessoes_df = load_data(QUERY_SESSOES)
    votos_df = load_data(QUERY_VOTOS)
    votos_raw_df = load_data(QUERY_VOTOS_RAW)

    # ---------------------------
    # FILTROS
    # ---------------------------
    st.sidebar.title("Filtros")
    pauta_filtro = st.sidebar.selectbox(
        "Pauta",
        ["Todas"] + pautas_df["TITULO"].tolist()
    )

    if pauta_filtro != "Todas":
        votos_df = votos_df[votos_df["TITULO"] == pauta_filtro]
        votos_raw_df = votos_raw_df[votos_raw_df["TITULO"] == pauta_filtro]
        sessoes_df = sessoes_df[sessoes_df["TITULO"] == pauta_filtro]

    # ---------------------------
    # MÉTRICAS
    # ---------------------------
    c1, c2, c3 = st.columns(3)
    c1.metric("Total de Pautas", pautas_df.shape[0])
    c2.metric("Sessões", sessoes_df.shape[0])
    c3.metric("Votos", votos_raw_df.shape[0])

    # ---------------------------
    # TABELAS
    # ---------------------------
    st.subheader("📌 Pautas")
    st.dataframe(pautas_df, use_container_width=True)

    st.subheader("🗓️ Sessões")
    st.dataframe(sessoes_df, use_container_width=True)

    # ---------------------------
    # RESULTADO CONSOLIDADO
    # ---------------------------
    st.subheader("🗳️ Resultado das Votações")

    pivot = votos_df.pivot_table(
        index="TITULO",
        columns="VOTO",
        values="TOTAL",
        fill_value=0
    )

    st.dataframe(pivot, use_container_width=True)
    st.bar_chart(pivot)

    # ---------------------------
    # VOTOS DETALHADOS
    # ---------------------------
    st.subheader("🔎 Votos Detalhados")
    st.dataframe(votos_raw_df, use_container_width=True)

    # ---------------------------
    # GRÁFICO TEMPORAL (CORRIGIDO)
    # ---------------------------
    st.subheader("📈 Votos por Dia")

    votos_raw_df["DATA"] = pd.to_datetime(votos_raw_df["VOTADO_EM"])

    votos_por_dia = (
        votos_raw_df
        .groupby(pd.Grouper(key="DATA", freq="D"))
        .size()
        .reset_index(name="TOTAL")
    )

    st.line_chart(votos_por_dia.set_index("DATA"))
