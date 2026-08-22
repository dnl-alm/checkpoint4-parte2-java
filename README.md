# Mercado Express API

API REST desenvolvida em **Spring Boot** para gerenciamento de produtos de um mercado (ex: meias, produtos de limpeza, frutas), com persistência em banco de dados **Oracle** e retorno de recursos no padrão **HATEOAS** (nível de maturidade 3 do modelo de Richardson).

**IDE utilizada:** IntelliJ IDEA

### Link Deploy:
`https://checkpoint4-java-mercadoexpress.onrender.com`

- Swagger:
  `https://checkpoint4-java-mercadoexpress.onrender.com/swagger-ui/index.html`

## Sumário

- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Modelo de dados](#modelo-de-dados)
- [Configuração do banco Oracle](#configuração-do-banco-oracle)
- [Como executar o projeto](#como-executar-o-projeto)
- [Endpoints da API](#endpoints-da-api)
- [Testes via Postman/Insomnia](#testes-via-postmaninsomnia)
- [HATEOAS — Nível de maturidade 3](#hateoas--nível-de-maturidade-3)

![PRINT1_SPRING](docs/images/specs_1.png)
![PRINT2_SPRING](docs/images/specs_2.png)

## Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java | Linguagem principal |
| Spring Boot | Framework da aplicação |
| Spring Web | Criação dos endpoints REST |
| Spring Data JPA | Persistência e acesso ao banco Oracle |
| Spring HATEOAS | Links de navegação (nível 3 de maturidade REST) |
| Lombok | Redução de boilerplate (getters, setters, builders, construtores) |
| Maven | Gerenciamento de dependências e build |
| Oracle Database (SQL Developer) | Banco de dados relacional |
| Tomcat embutido | Servidor de aplicação (porta 8082) |
| Postman / Insomnia | Testes manuais dos endpoints HTTP |

## Estrutura do projeto

```
src/main/java/br/com/mercadoexpress
├── domain/mercado
│   ├── Mercado.java           
│   └── MercadoAssembler.java   
├── controller
│   └── MercadoController.java  
├── service
│   └── MercadoService.java     
├── repository
│   └── MercadoRepository.java  
├── dto/request
│   └── MercadoRequest.java     
├── dto/response
│   └── MercadoResponse.java    
└── exception
    └── IdNaoEncontradoException.java
```

## Modelo de dados

Tabela `TDS_TB_mercado` no Oracle:

| Coluna | Tipo Java | Observação |
|---|---|---|
| ID | Long | Chave primária, gerada automaticamente (`GenerationType.IDENTITY`) |
| NOME | String | Nome do produto |
| TIPO | String | Categoria (ex: bebidas, limpeza, hortifruti) |
| SETOR | String | Setor/corredor do mercado |
| TAMANHO | Double | Tamanho/volume do produto |
| PRECO | Double | Preço unitário |

![BD_ORACLE](docs/images/BD_DEVELOPER_print.png)

## Como executar o projeto

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```
2. Configure as credenciais do Oracle no `application.properties`.
3. Rode a aplicação:
   ```bash
   mvn spring-boot:run
   ```
4. A API estará disponível em `http://localhost:8082`.

## Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/mercado` | Cria um novo produto |
| GET | `/mercado` | Lista todos os produtos (paginado) |
| GET | `/mercado/{id}` | Busca um produto pelo ID |
| PUT | `/mercado/{id}` | Atualiza um produto existente |
| DELETE | `/mercado/{id}` | Remove um produto pelo ID |

## Testes via Postman/Insomnia

Todos os testes abaixo foram feitos apontando para `http://localhost:8082`.

### 1. POST /mercado — Criar produto

**Corpo da requisição (JSON):**
```json
{
    "nome": "Água",
    "tipo": "bebidas",
    "setor": "A1",
    "tamanho": 1.5,
    "preco": 6.00
}
```

**Resposta esperada (201 Created):**
```json
{
    "id": 1,
    "nome": "Água",
    "tipo": "bebidas",
    "setor": "A1",
    "tamanho": 1.5,
    "preco": 6.0
}
```

![POST](docs/images/POST_mercado.png)

### 2. GET /mercado — Listar todos

Retorna uma página (`Page<MercadoResponse>`) com os produtos cadastrados no banco Oracle.

**Resposta esperada (200 OK):**
```json
{
    "content": [
        {
            "id": 1,
            "nome": "Água",
            "tipo": "bebidas",
            "setor": "A1",
            "tamanho": 1.5,
            "preco": 6.0
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
}
```

![GET_ALL](docs/images/GET_listarTodos_mercado.png)

### 3. GET /mercado/{id} — Buscar por ID

**Resposta esperada (200 OK) com links HATEOAS:**
```json
{
    "id": 1,
    "nome": "Água",
    "tipo": "bebidas",
    "setor": "A1",
    "tamanho": 1.5,
    "preco": 6.0,
    "_links": {
        "self": { "href": "http://localhost:8082/mercado/1" },
        "atualizar": { "href": "http://localhost:8082/mercado/1" },
        "deletar": { "href": "http://localhost:8082/mercado/1" }
    }
}
```

![GET_POR_ID](docs/images/GET_pesquisarPorId_mercado.png)

**Teste de erro — ID inexistente (ex: `/mercado/999`):**

### 4. PUT /mercado/{id} — Atualizar produto

**Corpo da requisição (JSON):**
```json
{
    "nome": "Água com gás",
    "tipo": "bebidas",
    "setor": "A1",
    "tamanho": 1.5,
    "preco": 7.00
}
```

**Resposta esperada (200 OK):**
```json
{
    "id": 1,
    "nome": "Água com gás",
    "tipo": "bebidas",
    "setor": "A1",
    "tamanho": 1.5,
    "preco": 7.0,
    "_links": {
        "self": { "href": "http://localhost:8082/mercado/1" }
    }
}
```

![PUT](docs/images/PUT_atualizar_mercado.png)

### 5. DELETE /mercado/{id} — Remover produto

**Resposta esperada:** `204 No Content`.

![DELETE](docs/images/DELETE_mercado.png)

## HATEOAS — Nível de maturidade 3

O projeto implementa o **nível 3 do Modelo de Maturidade de Richardson**, o mais alto, que combina:

1. **Recursos** (nível 1) — cada produto é acessado por sua própria URI (`/mercado/{id}`).
2. **Verbos HTTP** (nível 2) — GET, POST, PUT e DELETE são usados semanticamente corretos para cada operação.
3. **HATEOAS** (nível 3) — as respostas incluem links de navegação (`_links`) que informam ao cliente quais ações ele pode realizar a seguir a partir do recurso atual (autodescoberta da API), sem precisar conhecer as URIs de antemão.

Isso é feito através do `MercadoAssembler`, que monta um `EntityModel<MercadoResponse>` com os links `self`, `atualizar` e `deletar` para cada produto retornado.