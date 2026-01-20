import streamlit as st
import pandas as pd
import oracledb
from datetime import datetime

# ================================
# CONFIG
# ================================
st.set_page_config(page_title="Dashboard de Votação", layout="wide")

# ================================
# DB CONNECTION
# ================================
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
# ================================
# QUERIES
# ================================
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

# ================================
# LOAD DATA
# ================================
pautas_df = load_data(QUERY_PAUTAS)
sessoes_df = load_data(QUERY_SESSOES)
votos_df = load_data(QUERY_VOTOS)
votos_raw_df = load_data(QUERY_VOTOS_RAW)

# ================================
# SIDEBAR FILTERS
# ================================
st.sidebar.title("Filtros")

pauta_selecionada = st.sidebar.selectbox(
    "Pauta",
    options=["Todas"] + pautas_df["TITULO"].tolist()
)

if pauta_selecionada != "Todas":
    votos_df = votos_df[votos_df["TITULO"] == pauta_selecionada]
    votos_raw_df = votos_raw_df[votos_raw_df["TITULO"] == pauta_selecionada]
    sessoes_df = sessoes_df[sessoes_df["TITULO"] == pauta_selecionada]

# ================================
# METRICS
# ================================
st.title("📊 Dashboard – Sistema de Votação Cooperativa")

col1, col2, col3 = st.columns(3)

col1.metric("Total de Pautas", pautas_df.shape[0])
col2.metric("Total de Sessões", sessoes_df.shape[0])
col3.metric("Total de Votos", votos_raw_df.shape[0])

# ================================
# PAUTAS
# ================================
st.subheader("📌 Pautas")
st.dataframe(pautas_df, use_container_width=True)

# ================================
# SESSÕES
# ================================
st.subheader("🗓️ Sessões de Votação")
st.dataframe(sessoes_df, use_container_width=True)

# ================================
# RESULTADO DE VOTOS
# ================================
st.subheader("🗳️ Resultado das Votações")

pivot_votos = votos_df.pivot_table(
    index="TITULO",
    columns="VOTO",
    values="TOTAL",
    fill_value=0
).reset_index()

st.dataframe(pivot_votos, use_container_width=True)

st.bar_chart(
    pivot_votos.set_index("TITULO"),
    use_container_width=True
)

# ================================
# VOTOS DETALHADOS
# ================================
st.subheader("🔎 Votos Detalhados")
st.dataframe(votos_raw_df, use_container_width=True)

# ================================
# ANÁLISES
# ================================
st.subheader("📈 Análises")

votos_raw_df["DATA"] = pd.to_datetime(votos_raw_df["VOTADO_EM"]).dt.date
votos_por_dia = votos_raw_df.groupby("DATA").size().reset_index(name="TOTAL")

st.line_chart(
    votos_por_dia.set_index("DATA"),
    use_container_width=True
)
