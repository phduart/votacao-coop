import streamlit as st
import requests
import pandas as pd
import oracledb

# ==================================================
# CONFIGURAÇÃO GERAL
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

def safe_post(url, payload):
    try:
        status, resp = post(url, payload)
        if status in (200, 201):
            return True, resp
        else:
            st.warning(f"Erro {status}: {resp}")
            return False, resp
    except Exception as e:
        st.error(f"Erro ao conectar: {e}")
        return False, None

def safe_get(url):
    try:
        status, resp = get(url)
        if status == 200:
            return True, resp
        else:
            st.warning(f"Erro {status}: {resp}")
            return False, resp
    except Exception as e:
        st.error(f"Erro ao conectar: {e}")
        return False, None

# ==================================================
# LOAD DATA ORACLE
# ==================================================
@st.cache_data(ttl=60)
def load_data(query):
    # Abre a conexão dentro da função (evita cache quebrado)
    with oracledb.connect(user="votacao", password="votacao", dsn="localhost:1521/XEPDB1") as conn:
        df = pd.read_sql(query, conn)
    return df

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
# TABS PRINCIPAIS
# ==================================================
tab_dashboard, tab_operacional = st.tabs([
    "📊 Dashboard / Análises",
    "🛠️ Operacional (API)"
])

# ==================================================
# TAB DASHBOARD / ANÁLISES
# ==================================================
with tab_dashboard:
    st.header("📊 Dashboard – Votação Cooperativa")

    # Botão de refresh
    col_refresh, _ = st.columns([1, 9])
    with col_refresh:
        if st.button("🔄 Atualizar Dados"):
            st.cache_data.clear()
            st.success("Dados atualizados")

    # Carregar dados
    pautas_df = load_data(QUERY_PAUTAS)
    sessoes_df = load_data(QUERY_SESSOES)
    votos_df = load_data(QUERY_VOTOS)
    votos_raw_df = load_data(QUERY_VOTOS_RAW)

    # Métricas principais
    c1, c2, c3 = st.columns(3)
    c1.metric("Total de Pautas", pautas_df.shape[0])
    c2.metric("Total de Sessões", sessoes_df.shape[0])
    c3.metric("Total de Votos", votos_raw_df.shape[0])

    # Pautas e sessões
    col_pauta, col_sessao = st.columns(2)
    with col_pauta:
        st.subheader("📌 Pautas")
        st.dataframe(pautas_df, use_container_width=True)

    with col_sessao:
        st.subheader("🗓️ Sessões de Votação")
        st.dataframe(sessoes_df, use_container_width=True)

    # Resultado de votos
    st.subheader("🗳️ Resultado das Votações")
    pivot_votos = votos_df.pivot_table(
        index="TITULO",
        columns="VOTO",
        values="TOTAL",
        fill_value=0
    )

    col_res_tab, col_res_graph = st.columns([1, 1.5])
    with col_res_tab:
        st.markdown("**Números por Pauta**")
        st.dataframe(pivot_votos, use_container_width=True)

        # Votos por hora
        votos_raw_df['VOTADO_EM'] = pd.to_datetime(votos_raw_df['VOTADO_EM'])
        votos_raw_df['HORA'] = votos_raw_df['VOTADO_EM'].dt.hour
        votos_por_hora = votos_raw_df.groupby('HORA').size().reset_index(name='TOTAL_VOTOS')
        all_hours = pd.DataFrame({'HORA': range(0, 24)})
        votos_por_hora = all_hours.merge(votos_por_hora, on='HORA', how='left').fillna(0)
        st.subheader("⏰ Votos por Hora")
        st.bar_chart(votos_por_hora.set_index('HORA')['TOTAL_VOTOS'])

    with col_res_graph:
        st.markdown("**Distribuição de Votos**")
        st.bar_chart(pivot_votos)

        with st.expander("🔎 Votos Detalhados"):
            st.dataframe(votos_raw_df, use_container_width=True)

# ==================================================
# TAB OPERACIONAL (API)
# ==================================================
with tab_operacional:
    st.header("🛠️ Operações Diretas da API")

    def criar_pauta():
        with st.expander("📌 Criar Pauta", expanded=True):
            titulo = st.text_input("Título da Pauta", key="titulo_pauta")
            descricao = st.text_area("Descrição", key="descricao_pauta")
            if st.button("Criar Pauta"):
                if not titulo.strip():
                    st.warning("Título vazio")
                elif not descricao.strip():
                    st.warning("Descrição vazia")
                else:
                    success, resp = safe_post(f"{BASE_URL}/api/v1/pautas", {"titulo": titulo, "descricao": descricao})
                    if success:
                        st.success("Pauta criada!")
                        st.json(resp)

    def abrir_sessao():
        with st.expander("🕒 Abrir Sessão", expanded=True):
            pauta_id = st.number_input("ID da Pauta", min_value=1, step=1, key="sessao_pauta_id")
            minutos = st.number_input("Duração (min)", min_value=1, value=5, key="sessao_minutos")
            if st.button("Abrir Sessão"):
                success, resp = safe_post(f"{BASE_URL}/api/v1/pautas/{pauta_id}/sessao", {"minutos": minutos})
                if success:
                    st.success("Sessão aberta!")
                    st.json(resp)

    def registrar_voto():
        with st.expander("✍️ Registrar Voto", expanded=True):
            pauta_id_voto = st.number_input("ID da Pauta", min_value=1, step=1, key="voto_pauta_id")
            cpf = st.text_input("CPF do Associado", key="cpf_voto")
            voto = st.selectbox("Voto", ["SIM", "NAO"], key="voto_select")
            if st.button("Registrar Voto"):
                if not cpf.isdigit() or len(cpf) != 11:
                    st.warning("CPF inválido")
                else:
                    success, resp = safe_post(f"{BASE_URL}/api/v1/pautas/{pauta_id_voto}/votos", {"cpf": cpf, "voto": voto})
                    if success:
                        st.success("Voto registrado!")
                        st.json(resp)

    def buscar_resultado():
        with st.expander("📊 Resultado da Pauta", expanded=True):
            pauta_resultado = st.number_input("ID da Pauta", min_value=1, step=1, key="resultado_pauta_id")
            if st.button("Buscar Resultado"):
                success, resp = safe_get(f"{BASE_URL}/api/v1/pautas/{pauta_resultado}/resultado")
                if success:
                    st.success("Resultado encontrado!")
                    st.json(resp)

    col1, col2 = st.columns(2)
    with col1:
        criar_pauta()
        abrir_sessao()
    with col2:
        registrar_voto()
        buscar_resultado()
