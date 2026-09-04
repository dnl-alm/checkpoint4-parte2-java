# Mercado Express — Parte 2 (Interface Web + Segurança)

Continuação do projeto **Mercado Express**. Nesta parte, foi adicionada uma **interface Web** com **Thymeleaf**, reaproveitando toda a lógica de negócio já construída na Parte 1 (API REST), e implementada **autenticação com Spring Security**, com definição de rotas públicas e privadas.

**IDE utilizada:** IntelliJ IDEA

## Link Deploy
`https://checkpoint4-parte2-java-mercado-express.onrender.com`

**Link página WEB**  
`https://checkpoint4-parte2-java-mercado-express.onrender.com/login`

## Sumário

- [Relação com a Parte 1](#relação-com-a-parte-1)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Modelo de dados](#modelo-de-dados)
- [Autenticação e segurança](#autenticação-e-segurança)
- [Como executar o projeto](#como-executar-o-projeto)
- [Páginas e rotas da interface Web](#páginas-e-rotas-da-interface-web)
- [Fluxo de uso](#fluxo-de-uso)
- [Design da interface](#design-da-interface)

## Relação com a Parte 1

Este projeto **não substitui** a Parte 1 — ele reaproveita a mesma base de código (entidade `Mercado`, `MercadoRepository`, `MercadoService`, banco Oracle) e adiciona uma nova camada de apresentação em cima dela. A API REST original (`MercadoController`, endpoints em `/mercado`, retorno JSON com HATEOAS) continua existindo e funcionando exatamente como documentado no README da Parte 1.

A separação de responsabilidades foi feita assim:

| Camada | Controller | Retorno | Rota base |
|---|---|---|---|
| API REST (Parte 1) | `MercadoController` | JSON (HATEOAS) | `/mercado` |
| Interface Web (Parte 2) | `MercadoViewController` | HTML (Thymeleaf) | `/mercados` |

Os dois controllers chamam o **mesmo** `MercadoService` por baixo — a lógica de negócio (validação, conversão entre DTO e entidade, persistência) não foi duplicada em nenhum momento.

## Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java | Linguagem principal |
| Spring Boot | Framework da aplicação |
| Spring Web | Roteamento HTTP |
| Spring Data JPA | Persistência e acesso ao banco Oracle |
| Thymeleaf | Renderização de páginas HTML no servidor |
| Spring Security | Autenticação e autorização por sessão |
| BCrypt | Hash de senhas |
| Lombok | Redução de boilerplate (getters, setters, builders, construtores) |
| Maven | Gerenciamento de dependências e build |
| Oracle Database | Banco de dados relacional (mesmo da Parte 1) |
| Tomcat embutido | Servidor de aplicação (porta 8082) |

## Estrutura do projeto

```
src/main/java/br/com/mercadoexpress
├── domain/mercado
│   ├── Mercado.java
│   └── MercadoAssembler.java
├── domain/usuario
│   └── Usuario.java
├── controller
│   └── MercadoController.java          (Parte 1 — API REST)
├── web
│   ├── MercadoViewController.java      (Parte 2 — páginas HTML)
│   ├── MercadoFormData.java
│   ├── AutenticacaoController.java
│   └── CadastroForm.java
├── security
│   ├── SecurityConfig.java
│   └── AutenticacaoService.java
├── service
│   └── MercadoService.java
├── repository
│   ├── MercadoRepository.java
│   └── UsuarioRepository.java
├── dto/request
│   └── MercadoRequest.java
├── dto/response
│   └── MercadoResponse.java
└── exception
    └── IdNaoEncontradoException.java

src/main/resources
├── templates
│   ├── fragments/layout.html
│   ├── mercado/list.html
│   ├── mercado/form.html
│   └── auth/login.html
│   └── auth/cadastro.html
└── static
    ├── css/mercado.css
    └── js/cadastro.js
```

## Modelo de dados

Além da tabela `TDS_TB_mercado` (Parte 1), esta parte adiciona:

Tabela `TDS_TB_usuarios_mercado_express`:

| Coluna | Tipo Java | Observação |
|---|---|---|
| ID | Long | Chave primária, gerada automaticamente (`GenerationType.IDENTITY`) |
| USERNAME | String | Nome de usuário, único |
| SENHA | String | Hash BCrypt da senha (nunca texto puro) |

## Autenticação e segurança

A segurança foi implementada com **Spring Security clássico** (login por formulário + sessão), **sem JWT e sem OAuth2** — o requisito era apenas a definição de rotas públicas e privadas, que esse modelo já resolve de forma direta.

**Como funciona:**

1. O usuário se cadastra em `/cadastro`, informando usuário e senha.
2. A senha é transformada em um **hash BCrypt** (`PasswordEncoder`) antes de ser salva — o valor original nunca é armazenado, e o processo não é reversível.
3. A classe `AutenticacaoService` implementa `UserDetailsService`, servindo de ponte entre o Spring Security e o `UsuarioRepository`: ao fazer login, o Spring busca o usuário pelo `username` e compara o hash da senha informada com o hash salvo.
4. Após o login, o Spring Security cria uma **sessão autenticada** — as próximas requisições são reconhecidas automaticamente enquanto a sessão durar (até o logout).

**Regras de acesso (`SecurityConfig`):**

| Rota | Acesso |
|---|---|
| `/login` | Pública |
| `/cadastro` | Pública |
| `/css/**`, `/js/**` | Públicas (necessárias para as próprias telas de login/cadastro renderizarem) |
| Todas as demais rotas (incluindo `/mercados/**` e `/mercado/**`) | Exigem login |

> **Nota:** como a exigência era bloquear *todos* os endpoints exceto cadastro e login, isso inclui a própria API REST da Parte 1 (`/mercado`). Na prática, isso significa que testar a API pelo Postman/Insomnia agora exige autenticar antes: enviar um `POST /login` com `username` e `senha` como dados de formulário, guardar o cookie de sessão retornado, e reutilizá-lo nas requisições seguintes à API.

**Validação de senha:** ocorre em duas camadas — uma no `cadastro.js` (feedback imediato no navegador) e outra, obrigatória, no `AutenticacaoController` (confirmação de senha e verificação de usuário duplicado). A camada do servidor é a que realmente garante a integridade, já que a validação em JavaScript pode ser desabilitada ou contornada pelo usuário.

**Proteção CSRF:** habilitada por padrão pelo Spring Security. O Thymeleaf injeta automaticamente o token CSRF nos formulários de criar/editar/excluir produto, sem necessidade de alteração manual nos templates.

## Como executar o projeto

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```
2. Configure as credenciais do Oracle no `application.properties` (mesmo banco da Parte 1).
3. Rode a aplicação:
   ```bash
   mvn spring-boot:run
   ```
4. Acesse `http://localhost:8082/cadastro` para criar o primeiro usuário.
5. Faça login em `http://localhost:8082/login`.

## Páginas e rotas da interface Web

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/cadastro` | Formulário de criação de usuário | Público |
| POST | `/cadastro` | Processa o cadastro | Público |
| GET | `/login` | Formulário de login | Público |
| POST | `/login` | Processa a autenticação | Público |
| POST | `/logout` | Encerra a sessão | Autenticado |
| GET | `/mercados` | Lista os produtos cadastrados | Autenticado |
| GET | `/mercados/novo` | Formulário de novo produto | Autenticado |
| POST | `/mercados` | Cria um produto | Autenticado |
| GET | `/mercados/{id}/editar` | Formulário de edição, pré-preenchido | Autenticado |
| POST | `/mercados/{id}/atualizar` | Salva as alterações do produto | Autenticado |
| POST | `/mercados/{id}/excluir` | Remove o produto | Autenticado |

## Fluxo de uso

1. **Acesso sem login:** ao tentar abrir qualquer página protegida (ex: `/mercados`) sem estar autenticado, o usuário é redirecionado automaticamente para `/login`.
2. **Cadastro:** o usuário acessa `/cadastro`, informa um nome de usuário e uma senha (com confirmação), e é redirecionado para o login com uma mensagem de sucesso.
3. **Login:** o usuário informa usuário e senha; se corretos, é redirecionado para `/mercados`. Se incorretos, uma mensagem de erro é exibida.
4. **Listagem:** a tela principal mostra todos os produtos cadastrados, com um botão de acesso rápido para criar um novo.
5. **Criação:** o botão "+ Novo produto" leva a um formulário; ao salvar, o usuário retorna à listagem já com o novo item.
6. **Edição:** o link "Editar" em cada linha da tabela abre o mesmo formulário, pré-preenchido com os dados atuais.
7. **Exclusão:** o link "Excluir" pede confirmação antes de remover o produto e atualizar a listagem.
8. **Logout:** o botão "Sair" encerra a sessão e retorna à tela de login.

## Design da interface

O layout foi construído com CSS próprio (sem framework externo como Bootstrap), com uma identidade visual ligada ao tema do projeto:

- **Paleta de cores:** verde-mata (`#2F5233`) como cor primária, remetendo a hortifrúti/mercado; mostarda (`#D9A441`) como cor de destaque.
- **Tipografia:** `Fraunces` (serifada) para títulos e logotipo; `Inter` (sã serifa) para o restante do texto, priorizando legibilidade em uma interface funcional de CRUD.
- **Elemento de assinatura:** o preço de cada produto é exibido como uma "etiqueta" recortada (via `clip-path` em CSS), remetendo a uma tag de preço física.
- **Responsividade:** a tabela de produtos e o formulário se ajustam para telas menores através de media queries.