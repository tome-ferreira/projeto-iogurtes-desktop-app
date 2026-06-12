# AUTENTICACAO.md — Guia de Autenticação JWT

> Documentação técnica em Português Europeu sobre o fluxo completo de
> autenticação da aplicação desktop **Gestão de Iogurtes**.

---

## Visão Geral

A aplicação usa autenticação **JWT (JSON Web Token)** com algoritmo **HS256**,
validado pelo Spring Security no backend. O token é emitido pelo backend no
login e incluído automaticamente em todos os pedidos subsequentes via interceptor
do OkHttp.

```
Utilizador insere credenciais
    │
    ▼
PaginaLogin.handleLogin()
    │  POST /auth/login  { email, password }
    ▼
Backend valida credenciais → emite JWT com claim "role"
    │
    ▼
LoginResponse { id, nome, email, role, token }
    │
    ▼
SessionManager populado (userId, userName, userEmail, userRole, authToken)
    │
    ▼
Sidebar renderizada com visibilidade baseada em role
    │
    ▼
Pedidos subsequentes incluem automaticamente Authorization: Bearer <token>
    │
    ▼
Utilizador clica "Sair" → clearSession() → PaginaLogin
```

---

## 1. Login

### Endpoint

```
POST /auth/login
Content-Type: application/json
```

### Corpo do pedido — LoginRequest

Verificado via `GET http://localhost:8081/v3/api-docs` (schema `LoginRequest`):

| Campo      | Tipo   | Descrição                         |
|------------|--------|-----------------------------------|
| `email`    | String | Endereço de email do utilizador   |
| `password` | String | Palavra-passe em texto simples    |

```json
{
  "email": "admin@empresa.com",
  "password": "minhapalvra-passe"
}
```

### Corpo da resposta — LoginResponse

Verificado via `GET http://localhost:8081/v3/api-docs` (schema `LoginResponse`):

| Campo   | Tipo   | Descrição                                               |
|---------|--------|---------------------------------------------------------|
| `id`    | UUID   | Identificador único do utilizador autenticado           |
| `nome`  | String | Nome completo do utilizador                             |
| `email` | String | Endereço de email                                       |
| `role`  | String | Papel no sistema (ver secção 5 para valores possíveis)  |
| `token` | String | Token JWT (HS256) para usar nos pedidos seguintes       |

```json
{
  "id": "92826f54-5f8d-4e27-9362-2b3e555c18b2",
  "nome": "António Silva",
  "email": "antonio.silva@empresa.com",
  "role": "ADMIN",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Onde é chamado

`PaginaLogin.handleLogin()` em
`src/main/java/com/gestaoiogurtes/controllers/PaginaLogin.java`

```java
authService.login(email, password, state -> {
    if (state.isLoading())  { /* desactivar botão */ }
    if (state.isSuccess())  { /* guardar sessão e navegar */ }
    if (state.isError())    { /* mostrar erro */ }
});
```

### O que acontece com cada campo da resposta

| Campo da resposta | Acção no controller                        |
|-------------------|--------------------------------------------|
| `response.id`     | `SessionManager.setUserId(response.id)`    |
| `response.nome`   | `SessionManager.setUserName(response.nome)`|
| `response.email`  | `SessionManager.setUserEmail(response.email)` |
| `response.role`   | `SessionManager.setUserRole(response.role)`|
| `response.token`  | `SessionManager.setAuthToken(response.token)` |

Após estes sets, o `RetrofitClient` começa automaticamente a incluir o token
em todos os pedidos.

---

## 2. SessionManager

Classe singleton (`SessionManager.getInstance()`) que centraliza todos os dados
da sessão activa. Localização:
`src/main/java/com/gestaoiogurtes/utils/SessionManager.java`

### Campos disponíveis

| Campo       | Tipo   | Getter              | Setter                  |
|-------------|--------|---------------------|-------------------------|
| `userId`    | UUID   | `getUserId()`       | `setUserId(UUID)`       |
| `userName`  | String | `getUserName()`     | `setUserName(String)`   |
| `userEmail` | String | `getUserEmail()`    | `setUserEmail(String)`  |
| `userRole`  | String | `getUserRole()`     | `setUserRole(String)`   |
| `authToken` | String | `getAuthToken()`    | `setAuthToken(String)`  |

### Como ler de qualquer ficheiro

```java
import com.gestaoiogurtes.utils.SessionManager;

UUID   id    = SessionManager.getInstance().getUserId();
String role  = SessionManager.getInstance().getUserRole();
String name  = SessionManager.getInstance().getUserName();
String email = SessionManager.getInstance().getUserEmail();
String token = SessionManager.getInstance().getAuthToken();
```

### Ciclo de vida

| Evento  | Estado do SessionManager                              |
|---------|-------------------------------------------------------|
| Início  | Todos os campos `null`                                |
| Login   | Todos os campos populados com dados de `LoginResponse`|
| Logout  | `clearSession()` — todos os campos voltam a `null`    |

---

## 3. Envio do Token

### Como o interceptor do RetrofitClient funciona

`RetrofitClient` (singleton em `src/main/java/com/gestaoiogurtes/api/RetrofitClient.java`)
inclui um `OkHttp Interceptor` que é executado **antes de cada pedido HTTP**:

```java
httpClientBuilder.addInterceptor(chain -> {
    Request original = chain.request();
    String token = SessionManager.getInstance().getAuthToken();
    if (token != null && !token.isBlank()) {
        Request authenticated = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authenticated);
    }
    return chain.proceed(original);
});
```

### Formato exacto do header

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1p...
```

### Comportamento quando não há token (rotas públicas)

Antes do login, `SessionManager.getAuthToken()` devolve `null`.
O interceptor detecta `null` e **não adiciona o header** — o pedido `POST /auth/login`
é enviado sem `Authorization`, o que é correcto porque o backend o aceita com `permitAll`.

---

## 4. Logout

### O que o botão "Sair" faz

O botão "Sair" na `Sidebar.fxml` chama `handleSair()` em
`src/main/java/com/gestaoiogurtes/layout/Sidebar.java`:

```java
@FXML
private void handleSair() {
    SessionManager.getInstance().clearSession();
    NavigationHelper.navigateTo(app, "/fxml/paginas/PaginaLogin.fxml");
}
```

**Sequência:**
1. `clearSession()` — limpa todos os dados de sessão (ver abaixo)
2. Navegação para `PaginaLogin.fxml`

### clearSession() — o que é limpo

```java
public void clearSession() {
    this.userId    = null;
    this.userName  = null;
    this.userRole  = null;
    this.userEmail = null;
    this.authToken = null;
}
```

Após `clearSession()`:
- O interceptor do `RetrofitClient` deixa de enviar `Authorization: Bearer ...`
- Qualquer pedido HTTP subsequent devolverá `401 Unauthorized` (se tentado)
- A sidebar recalcula a visibilidade (role é `null`)

---

## 5. Autorização por Role

### Roles disponíveis

| Role            | Acesso                                      | Dashboard                      |
|-----------------|---------------------------------------------|-------------------------------|
| `ADMIN`         | Tudo                                        | `DashboardAdmin.fxml`          |
| `GESTOR`        | Tudo excepto Utilizadores e Empresas        | `DashboardGestor.fxml`         |
| `FUNCIONARIO_MP`| Fornecedores, Matérias Primas, Stock, etc.  | `DashboardFuncionarioMp.fxml`  |
| `FUNCIONARIO_OP`| Matérias Primas, Ordens Produção, Stock     | `DashboardFuncionarioOp.fxml`  |
| `CLIENTE`       | Catálogo, Encomendas próprias               | `Dashboard.fxml`               |

### Relação com o claim JWT

O backend (SecurityConfig) extrai o claim `"role"` do JWT e cria uma
`SimpleGrantedAuthority("ROLE_" + role)`.

```java
// SecurityConfig.jwtAuthenticationConverter() — lógica do backend:
String role = jwt.getClaimAsString("role");
return List.of(new SimpleGrantedAuthority("ROLE_" + role));
```

O `SessionManager.userRole` armazena exactamente o valor do campo `role` da
`LoginResponse` — o mesmo valor que está no claim JWT.

### Rotas protegidas por role — comportamento esperado

```java
// No SecurityConfig do backend:
.requestMatchers("/encomendas/**").hasAnyRole("ADMIN", "GESTOR")
```

Se um utilizador com role `FUNCIONARIO_MP` tentar aceder a `/encomendas/**`:
- O backend devolve **403 Forbidden**
- O `ApiQuery` emite `QueryState.error("Erro HTTP 403", null)`
- O controller apresenta a mensagem de erro ao utilizador
- **Não há redireccionamento automático** — é responsabilidade da UI não mostrar
  o botão/secção ao utilizador sem permissão (gerido pela `Sidebar` via RBAC)

> ⚠️ **Gap conhecido:** Tratamento global de 401/403 (ex: redireccionamento automático
> para login em caso de token expirado) não está implementado. É trabalho futuro.

---

## 6. Diagrama de Fluxo

```
┌─────────────┐
│  PaginaLogin│
│  (FXML)     │
└──────┬──────┘
       │ handleLogin() — POST /auth/login
       │ { email, password }
       ▼
┌─────────────────┐
│  AuthService    │
│  + ApiQuery     │
└──────┬──────────┘
       │ LoginResponse { id, nome, email, role, token }
       ▼
┌──────────────────┐
│ SessionManager   │◄──── clearSession() no logout
│ userId           │
│ userName         │
│ userEmail        │
│ userRole         │
│ authToken ───────┼──── lido em cada request pelo interceptor
└──────┬───────────┘
       │ navegarParaDashboard(role)
       ▼
┌──────────────────────┐
│ Dashboard (por role) │
│ + Sidebar            │
│   → RBAC visibility  │
└──────┬───────────────┘
       │ Requests autenticados
       ▼
┌──────────────────────────────────┐
│ RetrofitClient interceptor       │
│ → Authorization: Bearer <token> │
└──────────────────────────────────┘
       │ Utilizador clica "Sair"
       ▼
┌──────────────────┐
│ clearSession()   │
│ → PaginaLogin    │
└──────────────────┘
```

---

## Ficheiros Relevantes

| Ficheiro | Papel |
|----------|-------|
| [PaginaLogin.java](../src/main/java/com/gestaoiogurtes/controllers/PaginaLogin.java) | Controller de login — chama `AuthService` |
| [PaginaLogin.fxml](../src/main/resources/fxml/paginas/PaginaLogin.fxml) | Layout da página de login |
| [AuthService.java](../src/main/java/com/gestaoiogurtes/services/AuthService.java) | Serviço de autenticação (ApiQuery pattern) |
| [IAuthApiService.java](../src/main/java/com/gestaoiogurtes/api/services/IAuthApiService.java) | Interface Retrofit para POST /auth/login |
| [LoginRequest.java](../src/main/java/com/gestaoiogurtes/models/auth/LoginRequest.java) | DTO do pedido (email, password) |
| [LoginResponse.java](../src/main/java/com/gestaoiogurtes/models/auth/LoginResponse.java) | DTO da resposta (id, nome, email, role, token) |
| [SessionManager.java](../src/main/java/com/gestaoiogurtes/utils/SessionManager.java) | Singleton de sessão — armazena token e dados do utilizador |
| [RetrofitClient.java](../src/main/java/com/gestaoiogurtes/api/RetrofitClient.java) | Singleton Retrofit — interceptor de autorização JWT |
| [Sidebar.java](../src/main/java/com/gestaoiogurtes/layout/Sidebar.java) | Controller da sidebar — logout chama `clearSession()` |
