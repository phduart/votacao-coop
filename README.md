# API de Votação Cooperativa
![Java](https://img.shields.io/badge/Java-2C2C2C?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-1B5E20?style=for-the-badge&logo=springboot&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-7A0C0C?style=for-the-badge&logo=oracle&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-8B1A1A?style=for-the-badge&logo=redis&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-263238?style=for-the-badge&logo=gradle&logoColor=white)

![Docker](https://img.shields.io/badge/Docker-0D47A1?style=for-the-badge&logo=docker&logoColor=white)
![Streamlit](https://img.shields.io/badge/Streamlit-37474F?style=for-the-badge&logo=streamlit&logoColor=white)

![REST API](https://img.shields.io/badge/API%20REST-102027?style=for-the-badge&logo=swagger&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-1B5E20?style=for-the-badge&logo=openapiinitiative&logoColor=white)
---
Uma solução para gerenciar pautas, sessões de votação e votos em um sistema de cooperativismo. Cada associado possui um voto, e as decisões são tomadas através de assembleias virtuais.

O projeto utiliza **Java (Spring Boot)** para a API, **Oracle** como banco de dados, **Redis** para cache e fila de sessões, e um **Dashboard em Python** para visualização de dados.

## 🧰 Desenho de Solução

![img_1.png](img/solucao.png)

---

## 🛠️ Tecnologias e Stack

**Backend:**
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- Gradle
- Oracle Database
- Redis

**Frontend / Dashboard:**
- Python 3
- Streamlit
- Pandas
- Matplotlib / Plotly

**Outros:**
- Docker / Docker Compose
- OpenAPI 3.0
---

## 🚀 Como Rodar

### Construir artefato
Executar o comando abaixo para executar testes e gerar o jar executável do Spring boot:
```
 ./gradlew clean build
```

### Executar Docker Compose
Rodar o docker-compose para subir toda a infra e o serviço:
```
docker-compose up --build -d
```

### Mostrar logs do serviço
Rodar o comando abaixo para visualizar os logs do serviço:
```
docker logs -f votacaoapp
```

### Swagger
Abrir a seguinte URL no browser para visualizar o Swagger UI:
> http://localhost:8080/swagger-ui/index.html


### Dashboard
Abrir a seguinte URL no browser para visualizar o Dashboard :

> http://localhost:8501/


### Destruir o serviço
```
docker-compose down
```
---

## 📡 Endpoints da API

Base URL: `http://localhost:8080/api/v1`

### 📮 Collection Postman
```
src/resources/postman
```
![img.png](img/postman.png)

---

### 🔹 Pautas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/pautas` | Cadastrar nova pauta |
| POST   | `/pautas/{pautaId}/sessao` | Abrir sessão de votação |
| GET    | `/pautas/{pautaId}/resultado` | Obter resultado da votação |

#### 1️⃣ POST `/pautas` – Cadastrar nova pauta
**Request:**
```json
{
  "titulo": "Aumento do VR para 2026",
  "descricao": "Votação para decidir o reajuste de 10% no vale refeição."
}
```

**Response 201 Created:**
```json
{
  "id": 1,
  "titulo": "Aumento do VR para 2026",
  "descricao": "Votação para decidir o reajuste de 10% no vale refeição.",
  "criadaEm": "2026-01-21T12:00:00Z"
}
```

**Response 400 Bad Request:**  
Dados inválidos.

---

#### 2️⃣ POST `/pautas/{pautaId}/sessao` – Abrir sessão de votação
**Request:**
```json
{
  "minutos": 5
}
```
- `minutos` é opcional, padrão 1 minuto.

**Response 200 OK:**
```json
{
  "id": 1,
  "pautaId": 1,
  "abertaEm": "2026-01-21T12:05:00Z",
  "fechaEm": "2026-01-21T12:10:00Z",
  "status": "ABERTA"
}
```

**Response 404 Not Found:**  
Pauta não encontrada.

**Response 400 Bad Request:**  
Sessão já existente ou dados inválidos.

---

#### 3️⃣ GET `/pautas/{pautaId}/resultado` – Obter resultado
**Response 200 OK:**
```json
{
  "pautaId": 1,
  "resultado": {
    "SIM": 12,
    "NAO": 3
  }
}
```

**Response 404 Not Found:**  
Pauta não encontrada.

---

### 🔹 Votos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/pautas/{pautaId}/votos` | Registrar voto na pauta |

#### 4️⃣ POST `/pautas/{pautaId}/votos` – Registrar voto
**Request:**
```json
{
  "cpf": "12345678901",
  "voto": "SIM"
}
```

**Response 201 Created:**  
Voto registrado com sucesso.

**Response 400 Bad Request:**  
Voto duplicado ou sessão encerrada.

**Response 404 Not Found:**  
Pauta ou sessão não encontrada.

---

## 📊 Dashboard (Streamlit)
### Para acessar
> http://localhost:8501/

Exemplo de funcionalidades:
- Tabela de Pautas
- Tabela de Sessões 
- Tabela de Resultados des Votações
- Grafico de Votos por Sessão
- Gradico de Votos por Hora
- Tabela de Votos Detalhada

![img.png](img/dados.png)

--- 

## 📌 Validação de CPF
> ⚠️ **Atenção:** O fluxo contém uma chamada para uma API usando o Feing Cliente, porém a API pode ficar indisponível.
Caso a API não esteja online, é feito o cálculo de validação do CPF brasileiro.

![cpf.png](img/cpf.png)
> ℹ️ **Nota:** Utilização de Java Docs
---

## ⌚ Performance
> ℹ️ **Nota:** Foi realizado um teste de performance utilizando Apache JMeter em uma API Java, simulando carga concorrente sobre múltiplos endpoints REST.
> 
> ``Script .jmx em : src/resources/jmeter``
### 📌 Cenário do teste
````yaml
  Endpoints testados:               4 (POST e GET)
  Requisições por endpoint:         1.000
  Total de requisições:             4.000
  Tempo médio de resposta (geral):  ~1.5s
  Tempo máximo registrado:          ~5.3s
  Throughput total:                 ~384 requisições/segundo
  Taxa de erro:                     0%
  Payload médio:                    ~225 bytes
````
![teste.png](img/teste.png)

---

## ⭕ Painel de API (Streamlit)
### Para acessar
> http://localhost:8501/

API's:
- Criar Pauta
- Abrir Sessão
- Registrar Voto
- Resulta da Pauta

![painel.png](img/painel.png)

---

## 📂 Organização dos Pacotes

![pacotes.png](img/pacotes.png)

O projeto segue uma organização baseada em responsabilidade e domínio de negócio, inspirada em boas práticas de Clean Architecture e DDD.

- **controller**: Camada de entrada da aplicação (API REST).
- **service**: Contém as regras de negócio do sistema.
- **repository**: Camada de persistência e acesso ao banco de dados.
- **domain**: Representa o domínio da aplicação (entidades, DTOs e enums).
- **config**: Configurações gerais da aplicação (Swagger, Redis, CORS, etc).
- **scheduler**: Processos agendados, como encerramento automático de sessões.
- **exception**: Tratamento centralizado de exceções e erros.
- **mapper**: Conversão entre entidades e DTOs.
- **util**: Classes utilitárias reutilizáveis.

Essa organização melhora a legibilidade, facilita testes, manutenção e evolução do sistema.

---

## ✒ Autor do Projeto
``
``
[Paulo Duarte](mailto:ph.duartte97@gmail.com)

## 📄 Licença

MIT License