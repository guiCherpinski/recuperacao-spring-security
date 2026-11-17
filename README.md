# 🛡️ API Segura - Spring Security com JWT e RBAC

Projeto corporativo de **API RESTful segura** desenvolvida em **Java 21** e **Spring Boot 4**, implementando autenticação sem estado (*stateless*) via **JSON Web Token (JWT)**, controle de acesso baseado em papéis (**RBAC - Role-Based Access Control**), criptografia adaptativa com **BCrypt**, e tratamento padronizado de erros de segurança (401 e 403).

> 🎓 **Contexto Acadêmico:**  
> Projeto e relatório técnico desenvolvido como **Avaliação Prática de Spring Security** da Unidade Curricular de *Back-end* — Curso Técnico em Informática para Internet — **SENAI Santa Catarina**.  
> **Estudante:** Guilherme Gonçalves Cherpinski  
> **Docente:** Carlos Fábio Andrade  

---

## 📋 Sumário

- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura de Segurança](#-arquitetura-de-segurança)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar a Aplicação](#-como-executar-a-aplicação)
- [Endpoints da API](#-endpoints-da-api)
- [Roteiro de Testes no Postman / Insomnia / cURL](#-roteiro-de-testes-no-postman--insomnia--curl)
- [Testes Automatizados](#-testes-automatizados)
- [Boas Práticas e Conformidade (LGPD e OWASP)](#-boas-práticas-e-conformidade-lgpd-e-owasp)

---

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 21 (LTS)
* **Framework:** Spring Boot 4.1.2
* **Segurança:** Spring Security 7+
* **Autenticação:** JJWT 0.13.0 (`jjwt-api`, `jjwt-impl`, `jjwt-gson`)
* **Persistência de Dados:** Spring Data JPA / Hibernate ORM
* **Banco de Dados:** H2 Database (em memória para desenvolvimento e testes)
* **Validação:** Jakarta Bean Validation (Hibernate Validator)
* **Testes:** JUnit 5, MockMvc, JsonPath, Spring Boot Test

---

## 🔐 Arquitetura de Segurança

A aplicação foi desenhada seguindo as melhores práticas para APIs REST corporativas:

1. **Comunicação Stateless:** Não há armazenamento de sessão no servidor (`SessionCreationPolicy.STATELESS`). Cada requisição carrega sua própria credencial no cabeçalho `Authorization: Bearer <token>`.
2. **Cadeia de Filtros Personalizada (`SecurityFilterChain`):** O filtro `JwtAuthFilter` intercepta cada requisição antes do `UsernamePasswordAuthenticationFilter`, valida o token e popula o `SecurityContextHolder`.
3. **Controle de Acesso por Perfil (RBAC):**
   * `ADMIN`: Acesso irrestrito a todas as rotas da API e capacidade de cadastrar novos usuários.
   * `USER`: Acesso restrito a rotas de uso geral (`/api/**`).
4. **Defesa em Duas Camadas:**
   * Camada 1: Mapeamento de rotas via `authorizeHttpRequests` no `SecurityConfig`.
   * Camada 2: Proteção no nível de método com anotações `@PreAuthorize("hasRole('ADMIN')")`.
5. **Criptografia Forte:** Senhas armazenadas exclusivamente em formato de hash adaptativo **BCrypt** com salt individual gerado automaticamente.
6. **Respostas de Erro Padronizadas em JSON:**
   * `401 Unauthorized` via `RestAuthEntryPoint` (quando o token é ausente, inválido ou expirado).
   * `403 Forbidden` via `RestAccessDeniedHandler` (quando o usuário está autenticado, mas não possui o perfil exigido).
7. **CORS Consciente e CSRF Desabilitado:** CSRF seguro para desativação em APIs *stateless* sem cookies, e origens CORS explicitamente declaradas.

---

## 📂 Estrutura do Projeto

```text
src/
├── main/
│   ├── java/br/com/empresa/api/
│   │   ├── ApiSeguraApplication.java         # Classe principal de inicialização
│   │   ├── config/
│   │   │   ├── CorsConfig.java               # Configuração restritiva de CORS
│   │   │   ├── DadosIniciais.java            # Seed do administrador inicial (CommandLineRunner)
│   │   │   └── SecurityConfig.java           # Configuração do SecurityFilterChain e beans
│   │   ├── controller/
│   │   │   ├── AuthController.java           # Endpoints públicos de login e cadastro (ADMIN)
│   │   │   └── ExemploController.java        # Endpoints protegidos de teste (/api e /admin)
│   │   ├── dto/
│   │   │   ├── CadastroRequest.java          # Record para novo usuário com validações
│   │   │   ├── ErroResponse.java             # Record padronizado de erro em JSON
│   │   │   ├── LoginRequest.java             # Record de credenciais de login
│   │   │   └── TokenResponse.java            # Record com token JWT emitido
│   │   ├── entity/
│   │   │   ├── Perfil.java                   # Enum de perfis (ADMIN, USER)
│   │   │   └── Usuario.java                  # Entidade JPA implementando UserDetails
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java   # Tratamento global de Bean Validation e BadCredentials
│   │   ├── repository/
│   │   │   └── UsuarioRepository.java        # Repositório Spring Data JPA
│   │   └── security/
│   │       ├── JwtAuthFilter.java            # Filtro OncePerRequestFilter para ler o Bearer token
│   │       ├── RestAccessDeniedHandler.java  # Tratador JSON de erro 403
│   │       ├── RestAuthEntryPoint.java       # Tratador JSON de erro 401
│   │       ├── TokenService.java             # Geração, parsing e validação de tokens JWT
│   │       └── UsuarioDetailsService.java    # Busca de usuário no banco para o Spring Security
│   └── resources/
│       └── application.properties            # Propriedades da aplicação e segredos com fallbacks
└── test/
    ├── java/br/com/empresa/api/
    │   ├── ApiSeguraApplicationTests.java    # Teste de carregamento do contexto Spring Boot
    │   └── SegurancaIntegracaoTest.java      # 6 cenários de testes de integração com MockMvc
    └── resources/
        └── application.properties            # Propriedades isoladas para o ambiente de testes
```

---

## ⚙️ Pré-requisitos

Para compilar e rodar a aplicação localmente:

* **Java Development Kit (JDK):** Versão 21 ou superior
* **Maven:** Versão 3.9+ (ou utilizar o `./mvnw` embutido)
* **Cliente HTTP:** [Postman](https://www.postman.com/), Insomnia ou `curl`

---

## 🏃 Como Executar a Aplicação

### 1. Pela IDE (IntelliJ IDEA / VS Code / Eclipse)
1. Abra o projeto pela IDE selecionando o arquivo `pom.xml` (**Open as Project**).
2. Aguarde a sincronização e o download das dependências pelo Maven.
3. Localize e execute a classe `src/main/java/br/com/empresa/api/ApiSeguraApplication.java` clicando no botão **Run / Play**.
4. A aplicação inicializará na porta **`8080`**.

### 2. Pelo Terminal (Maven)
No diretório raiz do projeto, execute:

```bash
# No Windows (PowerShell / CMD):
.\mvnw.cmd spring-boot:run

# No Linux / macOS:
./mvnw spring-boot:run
```

### Configurações de Variáveis de Ambiente (Opcional):
O arquivo `application.properties` já possui valores padrão funcionais para desenvolvimento local. Em ambientes produtivos, você pode sobrescrevê-los via variáveis de ambiente:

| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `JWT_SECRET` | Chave de assinatura HMAC-SHA (mín. 32 caracteres) | *segredo-padrao-de-desenvolvimento...* |
| `JWT_EXPIRACAO_MS`| Tempo de expiração do JWT em milissegundos | `900000` (15 minutos) |
| `CORS_ORIGENS` | Lista de origens web permitidas | `http://localhost:3000` |
| `ADMIN_LOGIN` | Login do administrador criado no startup | `admin` |
| `ADMIN_SENHA` | Senha inicial do administrador | `SenhaAdmin@12345` |

---

## 📡 Endpoints da API

| Método | Endpoint | Acesso | Papel Necessário | Descrição |
| :---: | :--- | :---: | :---: | :--- |
| `POST` | `/auth/login` | **Público** | Qualquer | Autentica usuário e retorna o token JWT |
| `POST` | `/auth/registrar` | **Protegido** | `ADMIN` | Cadastra novo usuário com senha criptografada |
| `GET` | `/api/ola` | **Protegido** | `USER` ou `ADMIN` | Rota comum que retorna mensagem personalizada |
| `GET` | `/admin/painel` | **Protegido** | `ADMIN` | Rota administrativa restrita por perfil (RBAC) |

---

## 🧪 Roteiro de Testes no Postman / Insomnia / cURL

Abaixo está o roteiro ordenado para testar todas as camadas de segurança da API.

### 🔹 Teste 1: Acesso Anônimo a Rota Protegida (Erro 401)
* **Objetivo:** Garantir que rotas fechadas recusem requisições sem token.
* **Método:** `GET`
* **URL:** `http://localhost:8080/api/ola`
* **Headers:** Nenhum
* **Status Esperado:** `401 Unauthorized`
* **Resposta Esperada:**
```json
{
  "status": 401,
  "mensagem": "Autenticação necessária ou token inválido.",
  "dataHora": "2026-11-17T..."
}
```

---

### 🔹 Teste 2: Login com Credenciais Incorretas (Erro 401)
* **Objetivo:** Verificar se senhas incorretas são rejeitadas de forma segura.
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/login`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "login": "admin",
  "senha": "senha-totalmente-errada"
}
```
* **Status Esperado:** `401 Unauthorized`
* **Resposta Esperada:**
```json
{
  "status": 401,
  "mensagem": "Login ou senha inválidos.",
  "dataHora": "2026-11-17T..."
}
```

---

### 🔹 Teste 3: Login do Administrador Inicial (Sucesso 200)
* **Objetivo:** Autenticar com o administrador gerado automaticamente na inicialização e obter o token JWT.
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/login`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "login": "admin",
  "senha": "SenhaAdmin@12345"
}
```
* **Status Esperado:** `200 OK`
* **Resposta Esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiraEmSegundos": 900
}
```
> 📌 **Copie o token gerado!** Ele será chamado de `<TOKEN_ADMIN>`.

---

### 🔹 Teste 4: Administrador Acessa Rota Comum (Sucesso 200)
* **Objetivo:** Validar o envio do token no cabeçalho `Authorization: Bearer <token>`.
* **Método:** `GET`
* **URL:** `http://localhost:8080/api/ola`
* **Headers:** 
  * `Authorization: Bearer <TOKEN_ADMIN>`
* **Status Esperado:** `200 OK`
* **Resposta:**
```json
{
  "mensagem": "Olá, admin!"
}
```

---

### 🔹 Teste 5: Administrador Acessa Rota Administrativa (Sucesso 200)
* **Objetivo:** Confirmar que o perfil `ROLE_ADMIN` consegue acessar a rota com `@PreAuthorize("hasRole('ADMIN')")`.
* **Método:** `GET`
* **URL:** `http://localhost:8080/admin/painel`
* **Headers:** 
  * `Authorization: Bearer <TOKEN_ADMIN>`
* **Status Esperado:** `200 OK`
* **Resposta:**
```json
{
  "mensagem": "Painel administrativo."
}
```

---

### 🔹 Teste 6: Cadastrar Novo Usuário Comum (Sucesso 201)
* **Objetivo:** Demonstrar o cadastro de usuários restrito ao perfil ADMIN, com senha criptografada via BCrypt.
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/registrar`
* **Headers:** 
  * `Authorization: Bearer <TOKEN_ADMIN>`
  * `Content-Type: application/json`
* **Body:**
```json
{
  "login": "maria",
  "senha": "SenhaDaMaria@12345",
  "perfil": "USER"
}
```
* **Status Esperado:** `201 Created` *(Sem corpo de resposta)*

---

### 🔹 Teste 7: Login com o Novo Usuário Comum (Sucesso 200)
* **Objetivo:** Autenticar com o usuário comum recém-criado e gerar um token com perfil `ROLE_USER`.
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/login`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "login": "maria",
  "senha": "SenhaDaMaria@12345"
}
```
* **Status Esperado:** `200 OK`
* **Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiraEmSegundos": 900
}
```
> 📌 **Copie este novo token!** Ele será chamado de `<TOKEN_MARIA>`.

---

### 🔹 Teste 8: Usuário Comum Acessa Rota Comum (Sucesso 200)
* **Método:** `GET`
* **URL:** `http://localhost:8080/api/ola`
* **Headers:** 
  * `Authorization: Bearer <TOKEN_MARIA>`
* **Status Esperado:** `200 OK`
* **Resposta:**
```json
{
  "mensagem": "Olá, maria!"
}
```

---

### 🔹 Teste 9: Usuário Comum Tenta Acessar Painel Admin (Erro 403 Forbidden - Prova do RBAC)
* **Objetivo:** Comprovar que o Spring Security barra usuários autenticados que não possuem o perfil necessário.
* **Método:** `GET`
* **URL:** `http://localhost:8080/admin/painel`
* **Headers:** 
  * `Authorization: Bearer <TOKEN_MARIA>`
* **Status Esperado:** `403 Forbidden`
* **Resposta Esperada:**
```json
{
  "status": 403,
  "mensagem": "Você não tem permissão para acessar este recurso.",
  "dataHora": "2026-11-17T..."
}
```

---

### 🔹 Teste 10: Tentar Cadastrar Usuário com Login Duplicado (Erro 409 Conflict)
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/registrar`
* **Headers:** 
  * `Authorization: Bearer <TOKEN_ADMIN>`
  * `Content-Type: application/json`
* **Body:** Envie novamente os dados da `"maria"` (Teste 6)
* **Status Esperado:** `409 Conflict`

---

## 🧪 Testes Automatizados

O projeto conta com suíte completa de testes de integração automatizados em `src/test/java/br/com/empresa/api/SegurancaIntegracaoTest.java` utilizando **MockMvc** e banco de dados isolado em memória.

### Cenários Cobertos nos Testes:
1. `rotaProtegidaSemTokenRetorna401`: Requisições não autenticadas retornam status 401.
2. `loginComSenhaErradaRetorna401`: Senha incorreta devolve 401 sem vazar detalhes.
3. `loginValidoRetornaToken`: Credenciais corretas retornam 200 e token JWT válido.
4. `rotaComumComTokenRetorna200`: Token válido autentica requisição na rota `/api/ola`.
5. `rotaAdminComUsuarioComumRetorna403`: Perfil `USER` é barrado com 403 ao tentar rota `ADMIN`.
6. `rotaAdminComAdministradorRetorna200`: Perfil `ADMIN` acessa com sucesso a rota protegida.
7. `contextLoads`: Verificação do carregamento íntegro do contexto Spring Boot.

### Como Executar os Testes Automatizados:

Execute no terminal:
```bash
mvn test
```

**Resultado esperado:**
```text
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🛡️ Boas Práticas e Conformidade (LGPD e OWASP)

* **LGPD (Lei nº 13.709/2018):** Cumprimento do princípio de segurança e prevenção através do controle estrito de acesso e proteção de dados em repouso e trânsito.
* **Normas ABNT NBR ISO/IEC 27001 e 27002:** Implementação técnica de autenticação segura e controle de acesso baseado em privilégio mínimo.
* **OWASP API Security Top 10:** Mitigação de vulnerabilidades críticas como *BOLA (Broken Object Level Authorization)*, *Broken Authentication* e *Broken Function Level Authorization*.
* **Segurança de Senhas:** Utilização do algoritmo BCrypt com fator de trabalho configurável e salt aleatório, inviabilizando ataques por tabelas rainbow.

---

## 📄 Licença e Uso

Este projeto foi elaborado para fins educacionais e de demonstração prática de conceitos avançados de segurança em arquiteturas de microsserviços e APIs REST.
