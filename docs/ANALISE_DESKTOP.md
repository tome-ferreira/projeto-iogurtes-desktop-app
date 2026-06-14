# Análise Técnica da Aplicação Desktop — Gestão de Iogurtes

> **Nota para o aluno:** Este documento é uma referência técnica exaustiva destinada a ser usada como
> base para a redacção da secção **Frontend** do relatório académico. Não é o relatório — é a matéria
> prima para o escrever. Os trechos de código incluem caminhos de ficheiro e números de linha exactos
> para que qualquer afirmação possa ser verificada directamente no código-fonte.

---

## Índice

1. [Pilha Tecnológica (Stack)](#1-pilha-tecnológica-stack)
2. [Arquitectura Geral](#2-arquitectura-geral)
3. [Ponto de Entrada — `GestaoIogurtes`](#3-ponto-de-entrada--gestaoiogurtes)
4. [Padrão de Navegação — `NavigationHelper` e `AppAware`](#4-padrão-de-navegação--navigationhelper-e-appaware)
5. [Camada HTTP — `RetrofitClient`](#5-camada-http--retrofitclient)
6. [Gestão de Concorrência — `ApiQuery` e `QueryState`](#6-gestão-de-concorrência--apiquery-e-querystate)
7. [Modelo de Resposta Paginada — `PaginatedResponse`](#7-modelo-de-resposta-paginada--paginatedresponse)
8. [Gestão de Sessão — `SessionManager`](#8-gestão-de-sessão--sessionmanager)
9. [Autenticação e Login — `PaginaLogin`](#9-autenticação-e-login--paginalogin)
10. [Barra Lateral e RBAC — `Sidebar`](#10-barra-lateral-e-rbac--sidebar)
11. [Camada de Serviços em Dois Andares](#11-camada-de-serviços-em-dois-andares)
12. [Padrão CRUD nos Controllers de Página](#12-padrão-crud-nos-controllers-de-página)
13. [Padrão de Modal — `CriarEmpresaModalController`](#13-padrão-de-modal--criarempresamodalcontroller)
14. [Utilitários de Apresentação](#14-utilitários-de-apresentação)
    - 14.1 [MessageHelper](#141-messagehelper)
    - 14.2 [EnumDisplayHelper](#142-enumdisplayhelper)
    - 14.3 [DynamicColorHelper](#143-dynamiccolorhelper)
15. [Módulos de Negócio — Inventário Completo](#15-módulos-de-negócio--inventário-completo)
    - 15.1 [Autenticação](#151-autenticação)
    - 15.2 [Dashboards por Role](#152-dashboards-por-role)
    - 15.3 [Fornecedores](#153-fornecedores)
    - 15.4 [Matérias Primas](#154-matérias-primas)
    - 15.5 [Encomendas de Matéria Prima](#155-encomendas-de-matéria-prima)
    - 15.6 [Ordens de Produção](#156-ordens-de-produção)
    - 15.7 [Stock (Lotes de Produção)](#157-stock-lotes-de-produção)
    - 15.8 [Produtos Finais](#158-produtos-finais)
    - 15.9 [Encomendas (de clientes)](#159-encomendas-de-clientes)
    - 15.10 [Utilizadores](#1510-utilizadores)
    - 15.11 [Empresas](#1511-empresas)
    - 15.12 [Certificações](#1512-certificações)
    - 15.13 [Tipos de Fornecedor](#1513-tipos-de-fornecedor)
    - 15.14 [Tipos de Matéria Prima](#1514-tipos-de-matéria-prima)
    - 15.15 [Tipos de Pallet](#1515-tipos-de-pallet)
    - 15.16 [Moedas](#1516-moedas)
16. [Interfaces Retrofit — Mapeamento Completo de Endpoints](#16-interfaces-retrofit--mapeamento-completo-de-endpoints)
17. [Configuração da API — `config.properties`](#17-configuração-da-api--configproperties)
18. [Fluxos de Dados Ponta a Ponta](#18-fluxos-de-dados-ponta-a-ponta)
19. [Padrões Reutilizáveis Transversais](#19-padrões-reutilizáveis-transversais)

---

## 1. Pilha Tecnológica (Stack)

A aplicação é uma **aplicação de desktop** construída com **Java 17+** e **JavaFX 21**. A tabela seguinte lista todas as dependências relevantes e a sua função:

| Dependência | Versão (aprox.) | Função na aplicação |
|---|---|---|
| **JavaFX 21** | 21 | Framework de UI desktop; Scene Graph, FXML, CSS |
| **AtlantaFX** | 2.x | Biblioteca de temas e componentes para JavaFX (PrimerLight/Dark, Nord, Cupertino, Dracula) |
| **Retrofit 2** | 2.9+ | Cliente HTTP declarativo para comunicação REST com o backend Spring Boot |
| **OkHttp 3** | 3.x | Cliente HTTP subjacente ao Retrofit; fornece o interceptor de logging e o interceptor de JWT |
| **Gson** | 2.x | Serialização/deserialização JSON; configurado com adaptadores personalizados para `LocalDateTime` e `LocalDate` |
| **Ikonli (materialdesign2)** | - | Ícones vectoriais Material Design injectados programaticamente via `FontIcon` |

O ficheiro `pom.xml` (Maven) contém todas as declarações. A aplicação **não usa JavaFX TableView** para as grelhas de dados — as tabelas são construídas manualmente com `VBox`/`HBox` em Java puro, o que permite controlo total sobre estilo e conteúdo de cada célula.

---

## 2. Arquitectura Geral

A arquitectura segue um modelo em camadas adaptado ao JavaFX MVC:

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO (UI)                  │
│  FXML (layouts) + CSS (estilos) + Controllers (JavaFX)          │
│  src/main/resources/fxml/  +  src/main/java/.../controllers/    │
│  src/main/java/.../layout/   (Sidebar)                          │
│  src/main/java/.../components/  (modais CRUD)                   │
├─────────────────────────────────────────────────────────────────┤
│                    CAMADA DE SERVIÇO (Facade)                   │
│  src/main/java/.../services/  (XxxService.java)                 │
│  — encapsula ApiQuery + RetrofitClient                          │
├─────────────────────────────────────────────────────────────────┤
│                    CAMADA DE API (HTTP)                         │
│  src/main/java/.../api/RetrofitClient.java  (Singleton Retrofit)│
│  src/main/java/.../api/ApiQuery.java        (executor async)    │
│  src/main/java/.../api/QueryState.java      (máquina de estados)│
│  src/main/java/.../api/services/IXxxApiService.java  (interfaces│
│                                              Retrofit anotadas) │
├─────────────────────────────────────────────────────────────────┤
│                    CAMADA DE MODELOS (DTO)                      │
│  src/main/java/.../models/  (POJOs Java que espelham JSON)      │
├─────────────────────────────────────────────────────────────────┤
│                    UTILITÁRIOS TRANSVERSAIS                     │
│  SessionManager · NavigationHelper · MessageHelper              │
│  EnumDisplayHelper · DynamicColorHelper · AppAware              │
└─────────────────────────────────────────────────────────────────┘
                         ↕ HTTP/REST (JWT)
              Backend Spring Boot — porta 8081
```

**Princípio central de threading:** toda a comunicação HTTP é executada na thread pool do OkHttp. Os resultados regressam ao controller **sempre** na JavaFX Application Thread, graças ao `Platform.runLater()` dentro de `ApiQuery`. Os controllers **nunca** chamam `Platform.runLater()` directamente — esta separação é explicitamente documentada nos comentários do código.

---

## 3. Ponto de Entrada — `GestaoIogurtes`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/GestaoIogurtes.java` (54 linhas)

```java
// linha 14
public class GestaoIogurtes extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {           // linha 19
        this.stage = stage;
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet()); // linha 21

        var url = getClass().getResource("/fxml/paginas/PaginaLogin.fxml"); // linha 24
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        AppAware controller = loader.getController();
        controller.setApp(this);                                  // linha 30

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Gestão de Iogurtes");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public Stage getStage() { return stage; }                    // linha 51
}
```

**Pontos técnicos relevantes:**

- O tema padrão **PrimerLight** do AtlantaFX é aplicado globalmente na linha 21 antes de qualquer cena ser mostrada.
- A janela inicial tem resolução `1280×720` mas é imediatamente maximizada (`setMaximized(true)`).
- O controller da primeira página (`PaginaLogin`) recebe a referência da aplicação via `setApp(this)` (linha 30), antes de a janela ser tornada visível. Este padrão é universal em todos os controllers do projecto.
- O método `getStage()` (linha 51) é usado pelo `NavigationHelper` para substituir o root da cena durante a navegação.
- O `main()` convencional (linha 47) limita-se a delegar para `Application.launch()`.

---

## 4. Padrão de Navegação — `NavigationHelper` e `AppAware`

**Ficheiros:**
- `src/main/java/com/gestaoiogurtes/utils/NavigationHelper.java` (55 linhas)
- `src/main/java/com/gestaoiogurtes/utils/AppAware.java` (13 linhas)

### 4.1 Interface `AppAware`

```java
// AppAware.java — linha 10
public interface AppAware {
    void setApp(GestaoIogurtes app);
}
```

Todos os controllers de páginas completas implementam esta interface. Serve para que o `NavigationHelper` possa injectar a referência da aplicação principal após o `FXMLLoader` ter construído o grafo de cena.

### 4.2 `NavigationHelper.navigateTo()`

```java
// NavigationHelper.java — linha 34
public static void navigateTo(GestaoIogurtes app, String fxmlPath) {
    try {
        var url = NavigationHelper.class.getResource(fxmlPath);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof AppAware aware) {   // linha 45
            aware.setApp(app);
        }

        app.getStage().getScene().setRoot(root);      // linha 49
    } catch (IOException e) {
        throw new RuntimeException("Falha ao carregar FXML: " + fxmlPath, e);
    }
}
```

**Como funciona a navegação:**
1. O FXML é carregado a partir do classpath via `getResource()`.
2. Se o controller implementar `AppAware`, `setApp()` é chamado **antes** de o root ser substituído na cena.
3. `app.getStage().getScene().setRoot(root)` — não cria uma nova `Scene`. Substitui apenas o nó raiz da cena existente, preservando a janela e o `Stage`.

**Implicação prática:** não há histórico de navegação. Cada chamada a `navigateTo()` destrói o root anterior e carrega um novo. Não existe funcionalidade de "voltar atrás".

### 4.3 Chamadas típicas à navegação

```java
// Em Sidebar.java, linha 182
NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardAdmin.fxml");

// Em DashboardFuncionarioMp.java, linha 179
NavigationHelper.navigateTo(app, "/fxml/paginas/EncomendaMp.fxml");
```

Os caminhos FXML são sempre caminhos de classpath absolutos iniciados com `/fxml/paginas/`.

---

## 5. Camada HTTP — `RetrofitClient`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/api/RetrofitClient.java` (142 linhas)

`RetrofitClient` é um **Singleton thread-safe** que configura e fornece uma instância partilhada do cliente Retrofit.

### 5.1 Implementação do Singleton (Double-Checked Locking)

```java
// linha 66
private static volatile RetrofitClient instance;

public static RetrofitClient getInstance() {      // linha 72
    if (instance == null) {
        synchronized (RetrofitClient.class) {
            if (instance == null) {
                instance = new RetrofitClient();
            }
        }
    }
    return instance;
}
```

O campo `volatile` garante visibilidade entre threads; o `synchronized` garante atomicidade na criação. Esta é a implementação clássica de double-checked locking.

### 5.2 Adaptadores Gson para Tipos de Data/Hora

O Gson padrão não suporta `java.time.LocalDateTime` nem `java.time.LocalDate`. O `RetrofitClient` regista adaptadores personalizados:

```java
// linhas 45–60
Gson gson = new GsonBuilder()
    .registerTypeAdapter(LocalDateTime.class,
        (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
            String raw = json.getAsString();
            raw = raw.replaceAll("(\\.\\d{6})\\d+", "$1"); // linha 49: truncar microssegundos extras
            return LocalDateTime.parse(raw,
                DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]"));
        })
    .registerTypeAdapter(LocalDateTime.class,
        (JsonSerializer<LocalDateTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
    .registerTypeAdapter(LocalDate.class,
        (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
    .registerTypeAdapter(LocalDate.class,
        (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
    .create();
```

**Pormenor crítico (linha 49):** a regex `(\\.\\d{6})\\d+` → `$1` é necessária porque o backend Spring Boot pode devolver timestamps com precisão variável (7, 8 ou mais casas decimais), o que quebraria o parser padrão. A regex garante que apenas os primeiros 6 dígitos decimais são usados.

O padrão de formato `[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]` usa secções opcionais do `DateTimeFormatter` para aceitar qualquer número de casas decimais entre 1 e 6.

### 5.3 Interceptor JWT

```java
// linhas 99–109
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

O interceptor é **stateless** do ponto de vista do OkHttp — lê o token de `SessionManager` em cada pedido. Isto significa que:
- Antes do login: `getAuthToken()` devolve `null`; o pedido é enviado sem header (correcto para `POST /auth/login`).
- Após o login: todos os pedidos incluem automaticamente `Authorization: Bearer <token>`.
- Após o logout: `SessionManager.clearSession()` anula o token; os pedidos seguintes deixam de incluir o header.

### 5.4 Configuração do Timeout

```java
// linhas 91–94
OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
    .connectTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
    .writeTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS);
```

O valor de `ApiConfig.TIMEOUT` é lido de `config.properties` (valor: `30` segundos). O logging HTTP é activado se `ApiConfig.LOGGING_ENABLED == true` (também lido de `config.properties`).

### 5.5 Método `getService()`

```java
// linha 139
public <T> T getService(Class<T> serviceClass) {
    return retrofit.create(serviceClass);
}
```

Cada `XxxService.java` usa este método para obter o proxy Retrofit correspondente à sua interface anotada. O Retrofit cria um proxy dinâmico em tempo de execução que converte os métodos anotados em pedidos HTTP reais.

---

## 6. Gestão de Concorrência — `ApiQuery` e `QueryState`

### 6.1 `QueryState<T>`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/api/QueryState.java` (170 linhas)

`QueryState` é uma classe genérica imutável que modela o ciclo de vida completo de uma operação assíncrona. É inspirado no padrão React Query.

```java
// linhas 39–44
public enum Status {
    IDLE,     // nenhum pedido iniciado
    LOADING,  // pedido em curso
    SUCCESS,  // resposta recebida com sucesso
    ERROR     // pedido terminou com erro
}
```

**Campos da classe:**

```java
// linhas 50–53
private final Status    status;
private final T         data;         // preenchido apenas em SUCCESS
private final String    errorMessage; // preenchido apenas em ERROR
private final Throwable error;        // excepção original (pode ser null)
```

**Métodos de fábrica estáticos:**

```java
// linhas 76–111
public static <T> QueryState<T> idle()                              // → IDLE
public static <T> QueryState<T> loading()                          // → LOADING
public static <T> QueryState<T> success(T data)                    // → SUCCESS
public static <T> QueryState<T> error(String message, Throwable)   // → ERROR
```

**Predicados de conveniência:**

```java
// linhas 118–130
public boolean isLoading() { return status == Status.LOADING; }
public boolean isSuccess() { return status == Status.SUCCESS; }
public boolean isError()   { return status == Status.ERROR;   }
```

### 6.2 `ApiQuery.execute()`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/api/ApiQuery.java` (124 linhas)

```java
// linha 80
public static <T> void execute(Call<T> call, Consumer<QueryState<T>> onStateChange) {
    // 1. Notificar imediatamente que o pedido começou (na thread actual)
    onStateChange.accept(QueryState.loading());                        // linha 82

    // 2. Enfileirar na thread pool do OkHttp
    call.enqueue(new Callback<T>() {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                T body = response.body();
                Platform.runLater(() ->                                // linha 92
                    onStateChange.accept(QueryState.success(body)));
            } else {
                // Tenta extrair "message" do errorBody JSON
                String message = "Erro HTTP " + response.code();
                try {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        com.google.gson.JsonObject json = com.google.gson.JsonParser
                                .parseString(errorBody).getAsJsonObject();
                        if (json.has("message")) {                     // linha 101
                            message = json.get("message").getAsString();
                        }
                    }
                } catch (Exception ignored) {}
                final String finalMessage = message;
                Platform.runLater(() ->                                // linha 109
                    onStateChange.accept(QueryState.error(finalMessage, null)));
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            String message = t.getMessage() != null ? t.getMessage() : "Falha de rede desconhecida";
            Platform.runLater(() ->                                    // linha 119
                onStateChange.accept(QueryState.error(message, t)));
        }
    });
}
```

**Sequência temporal garantida:**
1. `QueryState.loading()` — emitido **imediatamente** na thread do chamador (JavaFX Application Thread).
2. O pedido HTTP é enfileirado na thread pool do OkHttp e retorna imediatamente.
3. Quando a resposta chega (noutra thread), `Platform.runLater()` agenda a entrega na JavaFX Application Thread.

**Extracção de mensagens de erro:** quando o backend devolve um código de erro HTTP (4xx, 5xx), o `ApiQuery` tenta analisar o body da resposta como JSON e extrair o campo `"message"`. Isto alinha-se com o formato de erros do Spring Boot, que tipicamente retorna `{"message": "...", "status": 400, ...}`. Se a extracção falhar, usa `"Erro HTTP " + código`.

---

## 7. Modelo de Resposta Paginada — `PaginatedResponse`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/models/PaginatedResponse.java` (21 linhas)

```java
// linhas 9–20
public class PaginatedResponse<T> {
    public List<T> content;        // lista de itens da página actual
    public int totalElements;      // total de registos no servidor
    public int totalPages;         // número total de páginas
    public int currentPage;        // página actual (base 0)
    public int pageSize;           // tamanho da página
    public boolean first;          // true se for a primeira página
    public boolean last;           // true se for a última página
}
```

Este POJO espelha exactamente a estrutura JSON devolvida pela API paginada do Spring Boot (que usa `Page<T>` internamente). Os campos `first` e `last` são usados directamente pelos controllers para activar/desactivar os botões de navegação de páginas:

```java
// Exemplo em FornecedoresController.java, linhas 149–150
if (btnAnterior != null) btnAnterior.setDisable(response.first);
if (btnProxima != null)  btnProxima.setDisable(response.last);
```

Nota: `setDisable(response.first)` significa que o botão "Anterior" é desactivado quando `first == true` (estamos na primeira página), e o botão "Próxima" é desactivado quando `last == true` (estamos na última página). Esta convenção é seguida uniformemente em todos os controllers.

---

## 8. Gestão de Sessão — `SessionManager`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/utils/SessionManager.java` (95 linhas)

`SessionManager` é um **Singleton simples** (não thread-safe por design — a UI é single-threaded) que armazena os dados da sessão activa do utilizador.

```java
// linhas 25–34
public class SessionManager {
    private static SessionManager instance;

    private UUID   userId;
    private String userRole;
    private String userName;
    private String userEmail;
    private String authToken;   // token JWT
```

**Campos armazenados:**

| Campo | Tipo | Origem | Uso |
|---|---|---|---|
| `userId` | `UUID` | `LoginResponse.id` | Identificação do utilizador |
| `userRole` | `String` | `LoginResponse.role` | Controlo de acesso por role (RBAC) |
| `userName` | `String` | `LoginResponse.nome` | Saudação personalizada nos dashboards |
| `userEmail` | `String` | `LoginResponse.email` | Informação de perfil |
| `authToken` | `String` | `LoginResponse.token` | Incluído automaticamente em todos os pedidos HTTP pelo interceptor do `RetrofitClient` |

**Método de limpeza de sessão (logout):**

```java
// linhas 88–94
public void clearSession() {
    this.userId    = null;
    this.userName  = null;
    this.userRole  = null;
    this.userEmail = null;
    this.authToken = null;
}
```

Este método é chamado pelo `Sidebar.handleSair()` antes de navegar para a página de login. Após a chamada, o interceptor JWT do `RetrofitClient` deixa de incluir o header `Authorization` nos pedidos.

**Leituras típicas nos controllers:**

```java
// Em DashboardFuncionarioMp.java, linhas 56–59
String nome = SessionManager.getInstance().getUserName();
String role = SessionManager.getInstance().getUserRole();
greetingLabel.setText(saudacao + ", " + nome + " — " + role);
```

```java
// Em FornecedoresController.java, linha 71
String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();
if ("FUNCIONARIO_MP".equals(role)) {
    btnNovo.setVisible(false);
    btnNovo.setManaged(false);
}
```

---

## 9. Autenticação e Login — `PaginaLogin`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/controllers/PaginaLogin.java` (119 linhas)

### 9.1 Campos FXML

```java
// linhas 27–30
@FXML private TextField     campoEmail;
@FXML private PasswordField campoPassword;
@FXML private Button        btnEntrar;
@FXML private Label         lblErro;
```

### 9.2 Fluxo de Login

```java
// linhas 44–81
@FXML
private void handleLogin() {
    String email    = campoEmail.getText();
    String password = campoPassword.getText();

    lblErro.setVisible(false);
    lblErro.setManaged(false);

    authService.login(email, password, state -> {
        if (state.isLoading()) {
            btnEntrar.setDisable(true);
            btnEntrar.setText("A entrar...");

        } else if (state.isSuccess()) {
            LoginResponse resposta = state.getData();

            // Utilizadores CLIENTE não têm acesso à aplicação desktop  // linha 62
            if ("CLIENTE".equals(resposta.role)) {
                mostrarErroLogin();
                return;
            }

            // Guardar todos os dados de sessão
            SessionManager session = SessionManager.getInstance();
            session.setUserId(resposta.id);
            session.setUserName(resposta.nome);
            session.setUserEmail(resposta.email);
            session.setUserRole(resposta.role);
            session.setAuthToken(resposta.token);

            // Navegar para o dashboard correcto conforme o role
            navegarParaDashboard(resposta.role);

        } else if (state.isError()) {
            mostrarErroLogin();
        }
    });
}
```

**Pontos a destacar:**

1. **Feedback imediato:** ao receber o estado `LOADING`, o botão é desactivado e o texto muda para `"A entrar..."`, dando feedback visual instantâneo.
2. **Exclusão de CLIENTES:** a aplicação desktop não é destinada a clientes — um utilizador com role `CLIENTE` é rejeitado mesmo que as credenciais estejam correctas (linha 62–65).
3. **Preenchimento do `SessionManager`:** após login bem-sucedido, cinco campos são guardados antes da navegação (linhas 68–73).

### 9.3 Navegação por Role após Login

```java
// linhas 105–117
private void navegarParaDashboard(String role) {
    switch (role) {
        case "ADMIN"          -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardAdmin.fxml");
        case "GESTOR"         -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardGestor.fxml");
        case "FUNCIONARIO_MP" -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioMp.fxml");
        case "FUNCIONARIO_OP" -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioOp.fxml");
        default               -> NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
    }
}
```

Cada role tem o seu próprio dashboard personalizado. Não existe um dashboard genérico activo — o `default` é um fallback de segurança.

---

## 10. Barra Lateral e RBAC — `Sidebar`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/layout/Sidebar.java` (326 linhas)

A `Sidebar` é um componente FXML reutilizável incluído em todas as páginas com `fx:include`. Implementa dois comportamentos principais: **controlo de acesso baseado em roles (RBAC)** e **navegação entre páginas**.

### 10.1 RBAC — `applyRoleBasedVisibility()`

```java
// linhas 125–171
private void applyRoleBasedVisibility() {
    String role = SessionManager.getInstance().getUserRole();  // linha 126

    // Esconder sempre o dashboard genérico
    hideNode(btnDashboard);                                    // linha 129

    // Esconder todos os dashboards de role específico por defeito
    hideNode(btnDashboardAdmin, btnDashboardGestor, btnDashboardMp, btnDashboardOp);  // linha 132

    if ("ADMIN".equals(role)) {
        btnDashboardAdmin.setVisible(true);
        btnDashboardAdmin.setManaged(true);
        // ADMIN vê tudo o resto

    } else if ("GESTOR".equals(role)) {
        btnDashboardGestor.setVisible(true);
        btnDashboardGestor.setManaged(true);
        hideNode(btnUtilizadores, btnEmpresas);                // linha 144

    } else if ("FUNCIONARIO_MP".equals(role)) {
        btnDashboardMp.setVisible(true);
        btnDashboardMp.setManaged(true);
        hideNode(
            btnOrdensProducao,
            lblSectionEncomendas, btnEncomendas,
            btnTiposMateriaPrima,
            btnCertificacoes, btnTiposFornecedor,
            lblSectionGestao, btnUtilizadores, btnEmpresas, btnTiposPallet, btnMoedas
        );

    } else if ("FUNCIONARIO_OP".equals(role)) {
        btnDashboardOp.setVisible(true);
        btnDashboardOp.setManaged(true);
        hideNode(
            lblSectionEncomendas, btnEncomendas,
            btnEncomendasMp, btnTiposMateriaPrima,
            lblSectionFornecedores, btnFornecedores, btnCertificacoes, btnTiposFornecedor,
            lblSectionGestao, btnUtilizadores, btnEmpresas, btnTiposPallet, btnMoedas
        );
    }
}
```

**Método `hideNode()`:**

```java
// linhas 116–123
private void hideNode(Node... nodes) {
    for (Node n : nodes) {
        if (n != null) {
            n.setVisible(false);   // ocultar visualmente
            n.setManaged(false);   // remover do layout (não ocupa espaço)
        }
    }
}
```

O uso conjunto de `setVisible(false)` + `setManaged(false)` é o padrão correcto em JavaFX para remover um nó tanto visualmente como do cálculo de layout.

### 10.2 Tabela de Visibilidade por Role

| Secção / Botão | ADMIN | GESTOR | FUNCIONARIO_MP | FUNCIONARIO_OP |
|---|:---:|:---:|:---:|:---:|
| Dashboard específico | ✓ | ✓ | ✓ | ✓ |
| Stock | ✓ | ✓ | ✓ | ✓ |
| Produtos Finais | ✓ | ✓ | ✓ | ✓ |
| Ordens de Produção | ✓ | ✓ | ✗ | ✓ |
| Encomendas (clientes) | ✓ | ✓ | ✗ | ✗ |
| Matérias Primas | ✓ | ✓ | ✓ | ✓ |
| Encomendas MP | ✓ | ✓ | ✓ | ✗ |
| Tipos de Matéria Prima | ✓ | ✓ | ✗ | ✗ |
| Fornecedores | ✓ | ✓ | ✓ | ✗ |
| Certificações | ✓ | ✓ | ✗ | ✗ |
| Tipos de Fornecedor | ✓ | ✓ | ✗ | ✗ |
| Utilizadores | ✓ | ✗ | ✗ | ✗ |
| Empresas | ✓ | ✗ | ✗ | ✗ |
| Tipos de Pallet | ✓ | ✓ | ✗ | ✗ |
| Moedas | ✓ | ✓ | ✗ | ✗ |

### 10.3 Funcionalidade de Temas

```java
// linhas 39–51
private final String[] temas = {
    new PrimerLight().getUserAgentStylesheet(),
    new PrimerDark().getUserAgentStylesheet(),
    new NordLight().getUserAgentStylesheet(),
    new NordDark().getUserAgentStylesheet(),
    new CupertinoLight().getUserAgentStylesheet(),
    new CupertinoDark().getUserAgentStylesheet(),
    new Dracula().getUserAgentStylesheet()
};
private int temaAtual = 0;

// linha 271
private void handleTema() {
    temaAtual = (temaAtual + 1) % temas.length;
    Application.setUserAgentStylesheet(temas[temaAtual]);
    temaTooltip.setText(nomesTemas[temaAtual]);
}
```

A mudança de tema é aplicada globalmente a toda a aplicação com `Application.setUserAgentStylesheet()`. O botão de tema cicla pelos 7 temas disponíveis do AtlantaFX.

### 10.4 Funcionalidade de Recolher/Expandir

```java
// linhas 36–37
private static final double LARGURA_EXPANDIDA = 240;
private static final double LARGURA_RECOLHIDA = 58;

// linhas 290–302
private void toggleSidebar() {
    expandida = !expandida;
    btnToggle.setGraphic(new FontIcon(
        expandida ? MaterialDesignC.CHEVRON_LEFT : MaterialDesignC.CHEVRON_RIGHT));
    root.setPrefWidth(expandida ? LARGURA_EXPANDIDA : LARGURA_RECOLHIDA);
    tituloLabel.setVisible(expandida);
    tituloLabel.setManaged(expandida);
    setLabelsVisiveis(expandida);
}
```

Quando recolhida (58px), apenas os ícones dos botões são visíveis. Quando expandida (240px), os rótulos de texto são mostrados.

---

## 11. Camada de Serviços em Dois Andares

A comunicação com a API segue um padrão de **dois andares**:

```
Controller
    │ chama métodos de negócio simples (getAll, create, update, delete)
    ▼
XxxService.java         ← primeiro andar: facade de negócio
    │ cria o Call Retrofit e delega para ApiQuery
    ▼
ApiQuery.execute()      ← segundo andar: executor assíncrono
    │ OkHttp thread pool → Platform.runLater()
    ▼
IXxxApiService.java     ← interface Retrofit anotada
    │ proxy dinâmico Retrofit → pedido HTTP
    ▼
Backend Spring Boot (porta 8081)
```

### 11.1 Exemplo — `FornecedorService`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/services/FornecedorService.java` (82 linhas)

```java
// linhas 20–24
public class FornecedorService {

    private IFornecedorApiService api() {
        return RetrofitClient.getInstance().getService(IFornecedorApiService.class);
    }
```

O método privado `api()` obtém o proxy Retrofit em cada chamada. O Retrofit cacheia internamente os proxies, pelo que isto não tem overhead significativo.

```java
// linhas 28–50
public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorResponse>>> cb) {
    ApiQuery.execute(api().findAll(page, size), cb);
}

public void getByTipo(String tipoId, int page, int size,
                      Consumer<QueryState<PaginatedResponse<FornecedorResponse>>> cb) {
    ApiQuery.execute(api().findAllByTipo(tipoId, page, size), cb);
}

public void create(CreateFornecedorRequest req, Consumer<QueryState<FornecedorResponse>> cb) {
    ApiQuery.execute(api().create(req), cb);
}

public void update(String id, UpdateFornecedorRequest req, Consumer<QueryState<FornecedorResponse>> cb) {
    ApiQuery.execute(api().update(id, req), cb);
}

public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
    ApiQuery.execute(api().delete(id), cb);
}
```

Cada método do serviço:
1. Chama `api()` para obter o proxy Retrofit.
2. Invoca o método da interface anotada (que cria o `Call<T>` mas não o executa ainda).
3. Passa o `Call<T>` para `ApiQuery.execute()` com o callback do controller.

Este padrão é absolutamente uniforme em todos os serviços do projecto:
`EncomendaMpService`, `MateriaPrimaService`, `UtilizadorService`, `OrdemProducaoService`, `LoteProducaoService`, `ProdutoFinalService`, `EncomendaService`, `EmpresaService`, `CertificacaoService`, `MoedaService`, `FornecedorTipoService`, `TipoMateriaPrimaService`, `TipoPalletService`, `AuthService`.

---

## 12. Padrão CRUD nos Controllers de Página

Todos os controllers de página (excepto dashboards) seguem um padrão idêntico. O `FornecedoresController` é o exemplo de referência indicado nos comentários do próprio código (por exemplo, `EncomendaMpController.java` refere explicitamente: *"Segue exactamente o padrão de FornecedoresController"*).

**Ficheiro de referência:** `src/main/java/com/gestaoiogurtes/controllers/FornecedoresController.java` (388 linhas)

### 12.1 Campos Comuns de Estado

```java
// linhas 39–44
private int currentPage = 0;
private int pageSize = 10;        // default: 10 itens por página
private int totalPages = 0;
private List<FornecedorResponse> todosItens = List.of();
private String selectedTipoId = null;  // filtro activo
```

### 12.2 Inicialização no `initialize()`

```java
// linhas 66–103
@FXML
public void initialize() {
    colorHelper = new DynamicColorHelper();
    carregarTipos();     // pré-carregar opções do filtro

    // RBAC: esconder botões de criação para FUNCIONARIO_MP
    String role = SessionManager.getInstance().getUserRole();  // linha 71
    if ("FUNCIONARIO_MP".equals(role)) {
        if (btnNovo != null) { btnNovo.setVisible(false); btnNovo.setManaged(false); }
        if (fab != null)     { fab.setVisible(false);     fab.setManaged(false);     }
    }

    // Listener no filtro por tipo
    cbFiltroTipo.valueProperty().addListener((obs, old, val) -> {   // linha 83
        if (val != null) {
            selectedTipoId = val.id;
            currentPage = 0;          // reset à primeira página ao filtrar
            carregarFornecedores();
        }
    });

    // Listener no tamanho da página
    cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> { // linha 94
        if (val != null && val != pageSize) {
            pageSize = val;
            currentPage = 0;
            carregarFornecedores();
        }
    });

    carregarFornecedores();  // carga inicial
}
```

### 12.3 Carregamento de Dados

```java
// linhas 128–134
private void carregarFornecedores() {
    if (selectedTipoId == null || selectedTipoId.isBlank()) {
        service.getAll(currentPage, pageSize, state -> handleState(state));
    } else {
        service.getByTipo(selectedTipoId, currentPage, pageSize, state -> handleState(state));
    }
}
```

### 12.4 Handler de Estado Unificado

```java
// linhas 136–162
private void handleState(QueryState<PaginatedResponse<FornecedorResponse>> state) {
    switch (state.getStatus()) {
        case LOADING -> setLoading(true);
        case SUCCESS -> {
            setLoading(false);
            var response = state.getData();
            if (response != null) {
                todosItens = response.content != null ? response.content : List.of();
                this.totalPages = response.totalPages;

                if (lblPagina != null) {
                    lblPagina.setText("Página " + (this.currentPage + 1) + " de " +
                                      Math.max(1, this.totalPages));        // linha 147
                }
                if (btnAnterior != null) btnAnterior.setDisable(response.first);
                if (btnProxima  != null) btnProxima.setDisable(response.last);
            } else {
                todosItens = List.of();
            }
            renderizarTabela();   // linha 154: re-renderizar a tabela
        }
        case ERROR -> {
            setLoading(false);
            mostrarNotificacao("Erro ao carregar fornecedores: " + state.getErrorMessage(), false);
        }
        default -> {}
    }
}
```

### 12.5 Renderização Imperativa da Tabela

A tabela não usa `TableView`. É construída programaticamente com `HBox` e `VBox`:

```java
// linhas 180–197
private void renderizarTabela() {
    tabelaContainer.getChildren().clear();   // limpar conteúdo anterior

    if (todosItens.isEmpty()) {
        tabelaContainer.getChildren().add(criarEstadoVazio());
        return;
    }

    tabelaContainer.getChildren().add(criarLinhaHeader()); // cabeçalho

    for (int i = 0; i < todosItens.size(); i++) {
        var linha = criarLinhaTabela(todosItens.get(i), i);
        if (i == todosItens.size() - 1) {
            linha.getStyleClass().add("tabela-linha-ultima"); // linha 193: estilo especial na última linha
        }
        tabelaContainer.getChildren().add(linha);
    }
}
```

### 12.6 Construção de uma Linha de Dados

```java
// linhas 232–321 (excerto)
private HBox criarLinhaTabela(FornecedorResponse item, int index) {
    var row = new HBox();
    row.getStyleClass().add("tabela-linha");
    row.setAlignment(Pos.CENTER_LEFT);

    // Coluna Nome (growable)
    var nomeLabel = new Label(item.nome != null ? item.nome : "—");
    nomeLabel.getStyleClass().add("celula-nome-principal");
    nomeLabel.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(nomeLabel, Priority.ALWAYS);   // linha 244: expande para preencher espaço disponível

    // Coluna Tipo (pill colorida dinamicamente)
    var tipoNome = (item.tipo != null && item.tipo.nome != null) ? item.tipo.nome : "Sem Tipo";
    var tipoLabel = new Label(tipoNome);
    String cor = colorHelper.getColorForType(tipoNome);   // linha 258: cor determinística por tipo
    tipoLabel.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; ...");

    // Botões de acção (condicionais por role)   linha 301
    String role = SessionManager.getInstance().getUserRole();
    var acoesBox = new HBox(6);
    acoesBox.getChildren().add(btnDetalhes);
    if (!"FUNCIONARIO_MP".equals(role)) {
        acoesBox.getChildren().add(btnEditar);
    }
    acoesBox.getChildren().add(btnCertificacoes);
    if (!"FUNCIONARIO_MP".equals(role)) {
        acoesBox.getChildren().add(btnEliminar);
    }

    // Avatar com iniciais
    var avatar = criarAvatar(item.nome);   // linha 318
    row.getChildren().addAll(avatar, nomeLabel, emailLabel, tipoBox, acoesBox);
    return row;
}
```

### 12.7 Avatar com Iniciais

O componente de avatar é idêntico em todos os controllers:

```java
// linhas 365–385 (FornecedoresController)
private StackPane criarAvatar(String nome) {
    String iniciais = extrairIniciais(nome);
    int cor = Math.abs((nome != null ? nome : "").hashCode()) % 4; // cor determinística pelo nome

    var texto = new Label(iniciais);
    texto.getStyleClass().addAll("avatar-texto", "avatar-texto-cor-" + cor);

    var pane = new StackPane(texto);
    pane.getStyleClass().addAll("avatar", "avatar-cor-" + cor);
    HBox.setMargin(pane, new Insets(0, 12, 0, 0));
    return pane;
}

private String extrairIniciais(String nome) {
    if (nome == null || nome.isBlank()) return "?";
    var partes = nome.trim().split("\\s+");
    if (partes.length == 1) {
        return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
    }
    return (partes[0].charAt(0) + "" + partes[partes.length - 1].charAt(0)).toUpperCase();
}
```

A cor do avatar é determinada pelo `hashCode()` do nome, módulo 4. Isto garante que o mesmo nome tem sempre a mesma cor (determinístico).

### 12.8 Loading Overlay

```java
// linhas 342–350
private void setLoading(boolean loading) {
    if (loadingOverlay != null) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setManaged(loading);
    }
    if (btnNovo != null) btnNovo.setDisable(loading);
    if (fab != null)     fab.setDisable(loading);
    if (cbFiltroTipo != null) cbFiltroTipo.setDisable(loading);
}
```

Durante o carregamento, um overlay visual é exibido e os controlos interactivos são desactivados.

### 12.9 Callback após Mutação

```java
// linhas 356–363
public void onMutacaoBemSucedida(String mensagem) {
    mostrarNotificacao(mensagem, true);   // toast verde
    carregarFornecedores();               // recarregar a lista
}

public void onMutacaoComErro(String mensagem) {
    mostrarNotificacao(mensagem, false);  // toast vermelho
}
```

Este par de métodos (`onMutacaoBemSucedida`, `onMutacaoComErro`) é o contrato entre os controllers de página e os modais de criação/edição/eliminação. Os modais recebem referências a estes métodos como `Consumer<String>`.

### 12.10 Estado Vazio

Quando a lista está vazia (sem dados ou sem resultados para o filtro activo), é mostrado um estado vazio:

```java
// linhas 323–340
private VBox criarEstadoVazio() {
    var icone = new FontIcon(MaterialDesignD.DOMAIN_OFF);  // ícone Material Design
    icone.setIconSize(52);
    icone.getStyleClass().add("estado-vazio-icone");

    var titulo = new Label("Nenhum fornecedor encontrado");
    titulo.getStyleClass().add("estado-vazio-titulo");

    var subtitulo = new Label("Ajusta o filtro ou clica em \"Criar Fornecedor\" para adicionar.");
    subtitulo.getStyleClass().add("estado-vazio-subtitulo");

    var caixa = new VBox(12, icone, titulo, subtitulo);
    caixa.getStyleClass().add("estado-vazio");
    VBox.setVgrow(caixa, Priority.ALWAYS);
    return caixa;
}
```

---

## 13. Padrão de Modal — `CriarEmpresaModalController`

**Ficheiro:** `src/main/java/com/gestaoiogurtes/components/empresas/CriarEmpresaModalController.java` (128 linhas)

Este é o padrão seguido por todos os modais de criação/edição/eliminação do projecto.

### 13.1 Método de Fábrica Estático `show()`

```java
// linhas 35–59
public static void show(EmpresaService service, Window owner, Consumer<String> onSuccess) {
    try {
        FXMLLoader loader = new FXMLLoader(
            CriarEmpresaModalController.class.getResource(
                "/fxml/components/empresas/CriarEmpresaModal.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);  // bloqueia a janela pai
        stage.initStyle(StageStyle.UTILITY);             // janela utilitária (sem maximizar/minimizar)
        stage.setResizable(false);
        stage.setTitle("Nova Empresa");

        Scene scene = new Scene(root);
        stage.setScene(scene);

        CriarEmpresaModalController controller = loader.getController();
        controller.setDialogStage(stage);
        controller.setOnSuccess(onSuccess);
        controller.setService(service);

        stage.showAndWait();   // linha 55: bloqueia até o modal fechar
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

**Características do modal:**
- `Modality.APPLICATION_MODAL` — bloqueia toda a interacção com a janela principal enquanto o modal está aberto.
- `StageStyle.UTILITY` — estilo de janela utilitária (sem botões de maximizar/minimizar na barra de título).
- `stage.showAndWait()` — execução bloqueante; só retorna quando o modal é fechado.
- O `Consumer<String> onSuccess` é o callback que, quando invocado, chama `onMutacaoBemSucedida(msg)` no controller pai.

### 13.2 Handler de Criação

```java
// linhas 84–126
@FXML
private void handleCriar() {
    lblErro.setText("");

    // 1. Leitura e validação dos campos
    String nome     = txtNome.getText() == null ? "" : txtNome.getText().trim();
    String nipc     = txtNipc.getText() == null ? "" : txtNipc.getText().trim();
    // ... outros campos ...

    if (nome.isEmpty() || nipc.isEmpty() || morada.isEmpty() || cp.isEmpty() || cidade.isEmpty()) {
        lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
        return;
    }

    // 2. Construir o DTO de pedido
    var request = new CreateEmpresaRequest(nome, nipc, telefone.isEmpty() ? null : telefone,
                                           morada, cp, cidade);

    // 3. Desactivar UI durante o pedido
    btnCriar.setDisable(true);
    btnCancelar.setDisable(true);

    // 4. Fazer o pedido assíncrono
    service.create(request, state -> {
        if (state.isLoading()) {
            btnCriar.setText("A criar...");
        } else if (state.isSuccess()) {
            dialogStage.close();
            onSuccess.accept("Empresa \"" + request.nomeEmpresa + "\" criada com sucesso."); // linha 117
        } else if (state.isError()) {
            btnCriar.setDisable(false);
            btnCancelar.setDisable(false);
            btnCriar.setText("Criar Empresa");
            lblErro.setText("Erro: " + state.getErrorMessage());
        }
    });
}
```

**Fluxo de estados no modal:**
- `LOADING`: botões desactivados, texto muda para "A criar...".
- `SUCCESS`: modal fecha, `onSuccess` é invocado com mensagem de confirmação para o controller pai exibir.
- `ERROR`: botões reactivados, mensagem de erro exibida no `lblErro` dentro do próprio modal.

---

## 14. Utilitários de Apresentação

### 14.1 MessageHelper

**Ficheiro:** `src/main/java/com/gestaoiogurtes/utils/MessageHelper.java` (91 linhas)

```java
// linhas 59–89
public static void mostrar(StackPane rootStack, String descricao, boolean sucesso) {
    if (rootStack == null || descricao == null) return;

    String titulo = sucesso ? "Sucesso" : "Erro";
    String textoLimpo = descricao.replaceAll("\\R", " ").strip(); // linha 64: normalizar quebras de linha

    var icone = new FontIcon(sucesso
            ? MaterialDesignD.DATABASE_CHECK_OUTLINE
            : MaterialDesignA.ALERT_CIRCLE_OUTLINE);

    var message = new Message(titulo, textoLimpo, icone);         // linha 70: componente AtlantaFX
    message.getStyleClass().add(sucesso ? Styles.SUCCESS : Styles.DANGER);

    // Botão de fechar manual
    message.setOnClose(e -> rootStack.getChildren().remove(message));

    // Auto-fechar após 4 segundos
    var timer = new PauseTransition(Duration.seconds(4));         // linha 80
    timer.setOnFinished(e -> rootStack.getChildren().remove(message));
    timer.play();

    StackPane.setAlignment(message, Pos.TOP_RIGHT);               // linha 85: posição no canto
    StackPane.setMargin(message, new Insets(16, 16, 0, 0));
    rootStack.getChildren().add(message);
}
```

**Características:**
- Usa o componente `Message` da biblioteca AtlantaFX.
- Aplica o estilo `Styles.SUCCESS` (verde) ou `Styles.DANGER` (vermelho).
- Auto-fecha após **4 segundos** via `PauseTransition` do JavaFX.
- O utilizador pode fechar manualmente clicando no botão ✕ (`setOnClose`).
- Posicionado no canto superior direito do `StackPane` raiz da página.
- O texto é normalizado (quebras de linha removidas) para evitar formatação estranha.

**Chamada típica:**

```java
// Em qualquer controller
MessageHelper.mostrar(rootStack, "Empresa criada com sucesso!", true);
MessageHelper.mostrar(rootStack, "Erro: " + state.getErrorMessage(), false);
```

### 14.2 EnumDisplayHelper

**Ficheiro:** `src/main/java/com/gestaoiogurtes/utils/EnumDisplayHelper.java` (188 linhas)

Converte valores de enums da API (letras maiúsculas com underscores) em texto legível em Português Europeu, e vice-versa.

**Grupos de enums suportados:**

| Enum | Valores da API | Valores em PT |
|---|---|---|
| Estado Físico | `LIQUIDO`, `SOLIDO` | `Líquido`, `Sólido` |
| Estado Encomenda | `PENDENTE`, `EXPEDIDA`, `CANCELADA` | `Pendente`, `Expedida`, `Cancelada` |
| Estado Encomenda MP | `PENDENTE`, `ENCOMENDADA`, `RECEBIDA`, `CANCELADA` | `Pendente`, `Encomendada`, `Recebida`, `Cancelada` |
| Estado Ordem Produção | `AGUARDA_APROVACAO`, `EM_PRODUCAO`, `CONCLUIDA`, `CANCELADA` | `Aguarda Aprovação`, `Em Produção`, `Concluída`, `Cancelada` |
| Estado Lote Produção | `DISPONIVEL`, `GASTO`, `DESPERDICIO` | `Disponível`, `Gasto`, `Desperdício` |

**Padrão de API para cada grupo:** três métodos (exemplo para Encomenda MP):

```java
// linhas 108–129
public static String estadoEncomendaMp(String valor)         // API → Label PT (para exibição)
public static String estadoEncomendaMpParaApi(String label)  // Label PT → API (para enviar filtro)
public static String[] estadoEncomendaMpLabels()             // lista de labels (para preencher ComboBox)
```

**Uso típico em inicialização de ComboBox:**

```java
// EncomendaMpController.java, linhas 71–76
cbFiltroEstado.getItems().add("Todos os Estados");
for (String label : EnumDisplayHelper.estadoEncomendaMpLabels()) {
    cbFiltroEstado.getItems().add(label);
}
cbFiltroEstado.valueProperty().addListener((obs, old, val) -> {
    selectedEstado = "Todos os Estados".equals(val)
            ? null
            : EnumDisplayHelper.estadoEncomendaMpParaApi(val); // converte para enviar à API
    currentPage = 0;
    carregarEncomendas();
});
```

**Conversão para CSS nos pills de estado:**

```java
// EncomendaMpController.java, linhas 241–244
String estadoLabel = EnumDisplayHelper.estadoEncomendaMp(item.estado); // ex: "Encomendada"
var estadoPill = new Label(estadoLabel);
estadoPill.getStyleClass().addAll("pill-estado", "pill-estado-" + estadoApiParaCss(item.estado));
// estadoApiParaCss("ENCOMENDADA") → "encomendada"  (minúsculas, underscores → hífens)
```

```java
// EncomendaMpController.java, linhas 311–314
private String estadoApiParaCss(String estado) {
    if (estado == null) return "desconhecido";
    return estado.toLowerCase().replace("_", "-");
    // "AGUARDA_APROVACAO" → "aguarda-aprovacao"
}
```

### 14.3 DynamicColorHelper

**Ficheiro:** `src/main/java/com/gestaoiogurtes/utils/DynamicColorHelper.java` (46 linhas)

```java
// linhas 7–44
public class DynamicColorHelper {
    private final Map<String, String> colorMap = new HashMap<>();
    private final Random random = new Random();

    private static final String[] COLORS = {
        "#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6",
        "#ec4899", "#14b8a6", "#f97316", "#06b6d4", "#6366f1",
        "#84cc16", "#d946ef", "#f43f5e", "#0ea5e9", "#047857",
        "#b45309", "#be123c", "#4338ca", "#0f766e"
    };

    public String getColorForType(String typeName) {
        if (typeName == null || typeName.isBlank()
                || typeName.equalsIgnoreCase("Sem Tipo")) {
            return "#9ca3af"; // cinzento para tipo desconhecido
        }
        return colorMap.computeIfAbsent(typeName, k -> {      // linha 40: lazy assignment
            int index = random.nextInt(COLORS.length);
            return COLORS[index];
        });
    }
}
```

**Funcionamento:** usa um `HashMap` como cache. Na primeira vez que um tipo é visto, uma cor aleatória é escolhida da paleta de 19 cores e guardada. Em acessos subsequentes com o mesmo tipo, a mesma cor é devolvida. Desta forma, dentro de uma sessão, cada tipo tem uma cor consistente, embora a cor varie entre sessões.

É usado nos `FornecedoresController` (para o tipo do fornecedor) e no `MateriasPrimasController` (para o tipo da matéria prima) para gerar pills coloridas para categorização visual.

---

## 15. Módulos de Negócio — Inventário Completo

### 15.1 Autenticação

- **Controller:** `PaginaLogin` (`controllers/PaginaLogin.java`)
- **Serviço:** `AuthService` (`services/AuthService.java`)
- **Interface API:** endpoint `POST /auth/login`
- **Modelo de resposta:** `LoginResponse` (`models/auth/LoginResponse.java`) — contém `id`, `nome`, `email`, `role`, `token`
- **Funcionalidade:** formulário de email+password, validação de role (CLIENTE rejeitado), preenchimento do `SessionManager`, navegação para dashboard por role

### 15.2 Dashboards por Role

Existem 4 dashboards específicos por role:

#### `DashboardAdmin`
- **Controller:** `controllers/DashboardAdmin.java`
- Funcionalidade: estatísticas globais do sistema para o administrador.

#### `DashboardGestor`
- **Controller:** `controllers/DashboardGestor.java`
- Funcionalidade: métricas de gestão operacional.

#### `DashboardFuncionarioMp`
- **Controller:** `controllers/DashboardFuncionarioMp.java` (217 linhas)
- **Serviços usados:** `EncomendaMpService`, `MateriaPrimaService`
- **Widgets no dashboard:**
  - Card "Pendentes" — contagem de encomendas MP com estado `PENDENTE`
  - Card "Em Trânsito" — contagem de encomendas MP com estado `ENCOMENDADA`
  - Card "Alertas de Stock" — matérias primas com `stockAtual <= stockMinimo`
  - Gráfico `PieChart` — distribuição de matérias primas por tipo

```java
// DashboardFuncionarioMp.java, linhas 86–96
long cPendentes = 0, cEmTransito = 0;
for (EncomendaMpResponse enc : response.content) {
    if (enc.estado == null) continue;
    if (enc.estado.equals("PENDENTE"))       cPendentes++;
    else if (enc.estado.equals("ENCOMENDADA")) cEmTransito++;
}
countPendentes.setText(String.valueOf(cPendentes));
countEmTransito.setText(String.valueOf(cEmTransito));
```

```java
// linhas 140–158: construção do PieChart
long cAlertas = response.content.stream()
    .filter(mp -> mp.stockAtual != null && mp.stockMinimo != null
               && mp.stockAtual <= mp.stockMinimo)
    .count();
countAlertas.setText(String.valueOf(cAlertas));

Map<String, Long> countPorTipo = response.content.stream()
    .collect(Collectors.groupingBy(
        mp -> (mp.tipo != null && mp.tipo.nome != null) ? mp.tipo.nome : "Desconhecido",
        Collectors.counting()));

ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
countPorTipo.forEach((tipo, count) -> pieData.add(new PieChart.Data(tipo, count)));
pieChartMaterias.setData(pieData);
```

#### `DashboardFuncionarioOp`
- **Controller:** `controllers/DashboardFuncionarioOp.java` (220 linhas)
- **Serviços usados:** `OrdemProducaoService`, `LoteProducaoService`
- **Widgets no dashboard:**
  - Card "Aguarda Aprovação" — ordens com estado `AGUARDA_APROVACAO`
  - Card "Em Produção" — ordens com estado `EM_PRODUCAO`
  - Card "Desperdício" — lotes com estado `DESPERDICIO`
  - `LineChart` — ordens concluídas por dia (últimos 5 dias)

```java
// DashboardFuncionarioOp.java, linhas 112–128
List<OrdemProducaoResponse> ordensConcluidas = response.content.stream()
    .filter(o -> "CONCLUIDA".equals(o.estado) && o.dataFim != null)
    .toList();

XYChart.Series<String, Number> series = new XYChart.Series<>();
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

for (int i = 4; i >= 0; i--) {
    LocalDate dia = LocalDate.now().minusDays(i);
    long count = ordensConcluidas.stream()
        .filter(o -> o.dataFim.toLocalDate().equals(dia))
        .count();
    series.getData().add(new XYChart.Data<>(dia.format(fmt), count));
}
lineChartOrdens.getData().clear();
lineChartOrdens.getData().add(series);
```

Os dashboards têm cards clicáveis que navegam para a página correspondente. Por exemplo:

```java
// DashboardFuncionarioMp.java, linhas 61–71
cardPendentes.setOnMouseClicked(e -> handleNavigateToEncomendaMp());
cardPendentes.setCursor(Cursor.HAND);
cardAlertas.setOnMouseClicked(e -> handleNavigateToMateriasPrimas());
cardAlertas.setCursor(Cursor.HAND);
```

### 15.3 Fornecedores

- **Controller:** `controllers/FornecedoresController.java` (388 linhas)
- **Serviço:** `FornecedorService`
- **Interface API:** `api/services/IFornecedorApiService.java`
- **Endpoints usados:** `GET /fornecedor` (paginado), `GET /fornecedor/tipo/{tipoId}` (filtro por tipo), `POST /fornecedor`, `PUT /fornecedor/{id}`, `DELETE /fornecedor/{id}`
- **Modais de componente:** `CriarFornecedorModalController`, `EditarFornecedorModalController`, `DetalhesFornecedorModalController`, `EliminarFornecedorModalController`, `CertificacoesFornecedorModalController`
- **Funcionalidades:**
  - Listagem paginada com filtro por tipo de fornecedor
  - Pill colorida para o tipo (via `DynamicColorHelper`)
  - RBAC: `FUNCIONARIO_MP` vê Detalhes e Certificações mas não pode Editar nem Eliminar

```java
// FornecedoresController.java, linhas 301–311
String role = SessionManager.getInstance().getUserRole();
var acoesBox = new HBox(6);
acoesBox.getChildren().add(btnDetalhes);
if (!"FUNCIONARIO_MP".equals(role)) {
    acoesBox.getChildren().add(btnEditar);
}
acoesBox.getChildren().add(btnCertificacoes);
if (!"FUNCIONARIO_MP".equals(role)) {
    acoesBox.getChildren().add(btnEliminar);
}
```

**Gestão de Certificações:** o `FornecedorService` também gere o sub-recurso de certificações, incluindo listagem, adição, actualização e remoção. A filtragem por `fornecedorId` é feita no cliente porque a API não suporta filtro por query parameter neste endpoint (comentado explicitamente em `IFornecedorApiService.java`, linha 57–60).

### 15.4 Matérias Primas

- **Controller:** `controllers/MateriasPrimasController.java` (338 linhas)
- **Serviço:** `MateriaPrimaService`, `TipoMateriaPrimaService`
- **Funcionalidades:**
  - Listagem paginada
  - Coluna "Stock / Mínimo" exibe o stock actual e o mínimo (num `VBox` com duas `Label`)
  - Pill colorida por tipo (via `DynamicColorHelper`)
  - RBAC: `FUNCIONARIO_MP` e `FUNCIONARIO_OP` não podem Editar, Criar nem Eliminar

```java
// MateriasPrimasController.java, linhas 173–184
VBox colStock = new VBox(2);
Label lblStockAtual = new Label(p.stockAtual != null
    ? String.valueOf(p.stockAtual) + " " + p.unidade : "—");
lblStockAtual.getStyleClass().add("celula-dados");

Label lblStockMinimo = new Label("Mín: " + (p.stockMinimo != null
    ? String.valueOf(p.stockMinimo) : "0"));
lblStockMinimo.getStyleClass().add("celula-nome-subtitulo");

colStock.getChildren().addAll(lblStockAtual, lblStockMinimo);
```

- **Modal especial:** `FornecedoresMateriaPrimaModalController` — lista os fornecedores associados a uma matéria prima específica.

### 15.5 Encomendas de Matéria Prima

- **Controller:** `controllers/EncomendaMpController.java` (362 linhas)
- **Serviço:** `EncomendaMpService`
- **Interface API:** `api/services/IEncomendaMpApiService.java`
- **Endpoints:** `GET /encomendas-mp`, `GET /encomendas-mp/estado/{estado}`, `GET /encomendas-mp/{id}`, `POST /encomendas-mp`, `PATCH /encomendas-mp/{id}/aprovar`, `PATCH /encomendas-mp/{id}/cancelar`, `PATCH /encomendas-mp/{id}/recebida`
- **Estados:** `PENDENTE`, `ENCOMENDADA`, `RECEBIDA`, `CANCELADA`
- **Funcionalidades:**
  - Filtro por estado via `EnumDisplayHelper`
  - Pill de estado com CSS dinâmico
  - Modal de detalhe com acções de transição de estado (aprovar, cancelar, marcar como recebida)
  - Total com IVA formatado a 2 casas decimais

```java
// EncomendaMpController.java, linhas 247–249
String totalStr = item.totalPrecoEurComIva != null
    ? String.format("%.2f €", item.totalPrecoEurComIva)
    : "—";
```

### 15.6 Ordens de Produção

- **Controller:** `controllers/OrdensProducaoController.java` (315 linhas)
- **Serviço:** `OrdemProducaoService`
- **Estados:** `AGUARDA_APROVACAO`, `EM_PRODUCAO`, `CONCLUIDA`, `CANCELADA`
- **Funcionalidades:**
  - Filtro por estado
  - Formatação de datas `LocalDateTime` com `DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")`
  - Modal de detalhe com acções de transição de estado

```java
// OrdensProducaoController.java, linha 48
private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

// linhas 263–265
private String formatarData(LocalDateTime dateTime) {
    if (dateTime == null) return "—";
    return dateTime.format(FORMATTER);
}
```

### 15.7 Stock (Lotes de Produção)

- **Controller:** `controllers/StockController.java` (274 linhas)
- **Serviço:** `LoteProducaoService`
- **Funcionalidades:**
  - Listagem paginada de lotes de produção
  - Lotes com estado `GASTO` têm estilo especial na linha

```java
// StockController.java, linhas 159–161
if ("GASTO".equals(item.estado)) {
    row.getStyleClass().add("linha-gasta");
}
```

- Formatação de datas de validade com `DateTimeFormatter.ofPattern("dd/MM/yyyy")`
- Stock em kg formatado a 2 casas decimais: `String.format("%.2f", item.stockAtualKg)`

### 15.8 Produtos Finais

- **Controller:** `controllers/ProdutosFinaisController.java` (304 linhas)
- **Serviços:** `ProdutoFinalService`, `MateriaPrimaService`
- **Colunas:** Código SKU, Nome
- **RBAC:** `FUNCIONARIO_MP` e `FUNCIONARIO_OP` só vêem Detalhes; Gestor e Admin podem Editar, Composição e Eliminar
- **Modal especial:** `ComposicaoModalController` — gere as matérias primas que compõem o produto final (receita)

```java
// ProdutosFinaisController.java, linhas 193–199
String role = SessionManager.getInstance().getUserRole();
boolean isFuncionario = "FUNCIONARIO_MP".equals(role) || "FUNCIONARIO_OP".equals(role);

colAcoes.getChildren().add(btnDetalhes);
if (!isFuncionario) {
    colAcoes.getChildren().addAll(btnEditar, btnComposicao, btnEliminar);
}
```

### 15.9 Encomendas (de clientes)

- **Controller:** `controllers/EncomendaController.java` (288 linhas)
- **Serviço:** `EncomendaService`
- **Estados:** `PENDENTE`, `EXPEDIDA`, `CANCELADA`
- **Colunas:** Data, Utilizador (cliente), Total (€), Estado
- **Acesso:** ADMIN, GESTOR (FUNCIONARIO_MP e FUNCIONARIO_OP não têm acesso conforme Sidebar)
- **Modal:** `DetalhesEncomendaModalController` — visualização e transição de estado da encomenda

```java
// EncomendaController.java, linhas 207–212
var estadoLabel = new Label(EnumDisplayHelper.estadoEncomenda(encomenda.estado));
estadoLabel.getStyleClass().add("celula-estado");
if (encomenda.estado != null) {
    estadoLabel.getStyleClass().add("estado-" + encomenda.estado.toLowerCase());
}
```

### 15.10 Utilizadores

- **Controller:** `controllers/UtilizadoresController.java` (524 linhas) — o controller mais complexo do projecto
- **Serviços:** `UtilizadorService`, `EmpresaService`
- **Interface API:** `api/services/IUtilizadorApiService.java`
- **Funcionalidades:**
  - **Duas abas:** Utilizadores Activos e Utilizadores Inactivos (duas tabelas independentes com paginação independente)
  - Filtro por role na aba de activos
  - Role pills com cor específica por role
  - Criação segregada por role: 5 modais diferentes (`CriarGestorModalController`, `CriarClienteModalController`, `CriarAdminModalController`, `CriarFuncionarioOpModalController`, `CriarFuncionarioMpModalController`)
  - Detalhes diferenciados por role: 5 modais de detalhe
  - Edição diferenciada por role: múltiplos modais de edição
  - Desactivação via `DesativarUtilizadorModalController`

```java
// UtilizadoresController.java, linhas 391–420
private Label criarRolePill(String role) {
    var pill = new Label(roleLegivel(role));
    pill.getStyleClass().addAll("role-pill", pilStyleClass(role));
    return pill;
}

private String roleLegivel(String role) {
    return switch (role) {
        case "ADMIN"          -> "Admin";
        case "GESTOR"         -> "Gestor";
        case "CLIENTE"        -> "Cliente";
        case "FUNCIONARIO_OP" -> "Funcionário OP";
        case "FUNCIONARIO_MP" -> "Funcionário MP";
        default               -> role;
    };
}

private String pilStyleClass(String role) {
    return switch (role) {
        case "ADMIN"          -> "pill-admin";
        case "GESTOR"         -> "pill-gestor";
        case "CLIENTE"        -> "pill-cliente";
        case "FUNCIONARIO_OP" -> "pill-funcionario-op";
        case "FUNCIONARIO_MP" -> "pill-funcionario-mp";
        default               -> "pill-desconhecido";
    };
}
```

**Navegação polimórfica para detalhes e edição:**

```java
// linhas 424–434
private void abrirDetalhes(UserResponse u, boolean inativo) {
    switch (u.role) {
        case "GESTOR"         -> DetalhesGestorModalController.show(u, inativo, window);
        case "CLIENTE"        -> DetalhesClienteModalController.show(u, inativo, window, empresaService);
        case "ADMIN"          -> DetalhesAdminModalController.show(u, inativo, window);
        case "FUNCIONARIO_OP" -> DetalhesFuncionarioOpModalController.show(u, inativo, window);
        case "FUNCIONARIO_MP" -> DetalhesFuncionarioMpModalController.show(u, inativo, window);
    }
}

// linhas 436–446
private void abrirEditar(UserResponse u) {
    switch (u.role) {
        case "GESTOR"         -> EditarGestorModalController.show(u, service, window, this::onMutacaoBemSucedida);
        case "CLIENTE"        -> EditarClienteModalController.show(u, service, empresaService, window, ...);
        case "ADMIN"          -> EditarAdminModalController.show(u, service, window, ...);
        case "FUNCIONARIO_OP",
             "FUNCIONARIO_MP" -> EditarFuncionarioModalController.show(u, service, window, ...);
    }
}
```

**Nota:** `FUNCIONARIO_OP` e `FUNCIONARIO_MP` partilham o mesmo modal de edição (`EditarFuncionarioModalController`).

**Endpoints da API para Utilizadores:**

```
GET  /users/active          → listar utilizadores ativos paginados
GET  /users/inactive        → listar utilizadores inativos paginados
GET  /users/gestores        → filtrar por role Gestor
GET  /users/clientes        → filtrar por role Cliente
GET  /users/admins          → filtrar por role Admin
GET  /users/funcionarios    → filtrar por role Funcionário
GET  /users/{id}            → obter por ID
DELETE /users/{id}          → soft-delete (desativar)
POST /users/gestores        → criar Gestor
PUT  /users/gestores/{id}   → actualizar Gestor
POST /users/clientes        → criar Cliente
PUT  /users/clientes/{id}   → actualizar Cliente
POST /users/admins          → criar Admin
PUT  /users/admins/{id}     → actualizar Admin
POST /users/funcionarios/op → criar Funcionário OP
POST /users/funcionarios/mp → criar Funcionário MP
PUT  /users/funcionarios/{id} → actualizar Funcionário
```

### 15.11 Empresas

- **Controller:** `controllers/EmpresasController.java`
- **Serviço:** `EmpresaService`
- **Modais:** `CriarEmpresaModalController`, `EditarEmpresaModalController`, `EliminarEmpresaModalController`
- **Acesso:** ADMIN (o GESTOR não vê no menu lateral)

### 15.12 Certificações

- **Controller:** `controllers/CertificacoesController.java` (306 linhas)
- **Serviço:** `CertificacaoService`
- **Funcionalidade extra:** pesquisa textual local (client-side) no campo `campoPesquisa`

```java
// CertificacoesController.java, linha 53
campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela(val.trim().toLowerCase()));

// linhas 132–138
private void filtrarTabela(String pesquisa) {
    var filtrados = todasCertificacoes.stream()
        .filter(c -> pesquisa.isEmpty()
                || (c.nome != null && c.nome.toLowerCase().contains(pesquisa)))
        .toList();
    // ...
}
```

**Comportamento único:** clicar numa linha (fora dos botões de acção) abre o modal de detalhes. Implementado com detecção de bubbling do evento:

```java
// linhas 219–238
row.setOnMouseClicked(e -> {
    if (e.getClickCount() == 1 && e.getTarget() instanceof Node node) {
        boolean isActionButton = false;
        Node current = node;
        while (current != null && current != row) {
            if (current instanceof Button) { isActionButton = true; break; }
            current = current.getParent();
        }
        if (!isActionButton) {
            DetalhesCertificacaoModalController.show(certificacao, window);
        }
    }
});
```

### 15.13 Tipos de Fornecedor

- **Controller:** `controllers/FornecedoresTipoController.java` (306 linhas)
- **Serviço:** `FornecedorTipoService`
- **Funcionalidade:** pesquisa local (filtro client-side), CRUD completo, linha clicável para detalhes

### 15.14 Tipos de Matéria Prima

- **Controller:** `controllers/TiposMateriaPrimaController.java` (295 linhas)
- **Serviço:** `TipoMateriaPrimaService`
- **Funcionalidade:** pesquisa local, CRUD completo, cursor `HAND` nas linhas, linha clicável

### 15.15 Tipos de Pallet

- **Controller:** `controllers/TiposPalletController.java` (318 linhas)
- **Serviço:** `TipoPalletService`
- **Colunas:** Nome, Capacidade (Kg)
- **Funcionalidade:** pesquisa local (filtro por nome e capacidade), CRUD completo

### 15.16 Moedas

- **Controller:** `controllers/MoedasController.java` (303 linhas)
- **Serviço:** `MoedaService`
- **Colunas:** Moeda (nome), Código ISO, Símbolo, Taxa (EUR)
- **Funcionalidade:** pesquisa local (por nome, código e símbolo), CRUD completo

```java
// MoedasController.java, linhas 132–137
private void filtrarTabela(String pesquisa) {
    var filtrados = todasMoedas.stream()
        .filter(m -> pesquisa.isEmpty()
                || m.nome.toLowerCase().contains(pesquisa)
                || m.codigo.toLowerCase().contains(pesquisa)
                || m.simbolo.toLowerCase().contains(pesquisa))
        .toList();
```

```java
// linha 208
var taxaLabel = new Label(moeda.taxaConversaoEur != null
    ? String.format("%.4f", moeda.taxaConversaoEur) : "—");
```

---

## 16. Interfaces Retrofit — Mapeamento Completo de Endpoints

### `IFornecedorApiService` (`api/services/IFornecedorApiService.java`, 96 linhas)

```java
@GET("fornecedor")                    Call<PaginatedResponse<FornecedorResponse>> findAll(page, size)
@GET("fornecedor/{id}")               Call<FornecedorResponse> findById(id)
@GET("fornecedor/tipo/{tipoId}")      Call<PaginatedResponse<FornecedorResponse>> findAllByTipo(tipoId, page, size)
@POST("fornecedor")                   Call<FornecedorResponse> create(@Body CreateFornecedorRequest)
@PUT("fornecedor/{id}")               Call<FornecedorResponse> update(id, @Body UpdateFornecedorRequest)
@DELETE("fornecedor/{id}")            Call<ResponseBody> delete(id)
@GET("fornecedor-tipos")              Call<PaginatedResponse<FornecedorTipoResponse>> findAllTipos(page, size)
@GET("fornecedores-certificacoes")    Call<PaginatedResponse<FornecedorCertificacaoResponse>> findAllCertificacoes(page, size)
@POST("fornecedor/{fornecedorId}/certificacoes") Call<FornecedorCertificacaoResponse> addCertificacao(id, @Body)
@PUT("fornecedor/certificacoes/{id}") Call<FornecedorCertificacaoResponse> updateCertificacao(id, @Body)
@DELETE("fornecedor/certificacoes/{id}") Call<ResponseBody> removeCertificacao(id)
@GET("certificacoes")                 Call<PaginatedResponse<CertificacaoResponse>> findAllCertificacoesDisponiveis(page, size)
```

### `IUtilizadorApiService` (`api/services/IUtilizadorApiService.java`, 119 linhas)

```java
@GET("users/active")          Call<PaginatedResponse<UserResponse>> findAllActive(page, size)
@GET("users/inactive")        Call<PaginatedResponse<UserResponse>> findAllInactive(page, size)
@GET("users/gestores")        Call<PaginatedResponse<UserResponse>> findGestores(page, size)
@GET("users/clientes")        Call<PaginatedResponse<UserResponse>> findClientes(page, size)
@GET("users/admins")          Call<PaginatedResponse<UserResponse>> findAdmins(page, size)
@GET("users/funcionarios")    Call<PaginatedResponse<UserResponse>> findFuncionarios(page, size)
@GET("users/{id}")            Call<UserResponse> findById(id)
@DELETE("users/{id}")         Call<ResponseBody> softDelete(id)
@POST("users/gestores")       Call<UserResponse> createGestor(@Body CreateGestorRequest)
@PUT("users/gestores/{id}")   Call<UserResponse> updateGestor(id, @Body UpdateGestorRequest)
@POST("users/clientes")       Call<UserResponse> createCliente(@Body CreateClienteRequest)
@PUT("users/clientes/{id}")   Call<UserResponse> updateCliente(id, @Body UpdateClienteRequest)
@POST("users/admins")         Call<UserResponse> createAdmin(@Body CreateAdminRequest)
@PUT("users/admins/{id}")     Call<UserResponse> updateAdmin(id, @Body UpdateAdminRequest)
@POST("users/funcionarios/op") Call<UserResponse> createFuncionarioOp(@Body CreateFuncionarioRequest)
@POST("users/funcionarios/mp") Call<UserResponse> createFuncionarioMp(@Body CreateFuncionarioRequest)
@PUT("users/funcionarios/{id}") Call<UserResponse> updateFuncionario(id, @Body UpdateFuncionarioRequest)
```

### `IEncomendaMpApiService` (`api/services/IEncomendaMpApiService.java`, 57 linhas)

```java
@GET("encomendas-mp")                        Call<PaginatedResponse<EncomendaMpResponse>> findAll(page, size)
@GET("encomendas-mp/estado/{estado}")        Call<PaginatedResponse<EncomendaMpResponse>> findByEstado(estado, page, size)
@GET("encomendas-mp/{id}")                   Call<EncomendaMpResponse> findById(id)
@POST("encomendas-mp")                       Call<EncomendaMpResponse> create(@Body CreateEncomendaMpRequest)
@PATCH("encomendas-mp/{id}/aprovar")         Call<EncomendaMpResponse> aprovar(id)
@PATCH("encomendas-mp/{id}/cancelar")        Call<EncomendaMpResponse> cancelar(id)
@PATCH("encomendas-mp/{id}/recebida")        Call<EncomendaMpResponse> marcarRecebida(id)
```

**Nota importante:** as interfaces Retrofit **não têm barra inicial** nos paths (`"encomendas-mp"` e não `"/encomendas-mp"`). O Retrofit concatena estes paths à `BASE_URL`. Esta convenção é crítica para o correcto funcionamento.

---

## 17. Configuração da API — `config.properties`

**Ficheiro:** `src/main/resources/config.properties` (11 linhas)

```properties
# Base URL — deve terminar com barra
api.base.url=http://localhost:8081/

# Timeout HTTP em segundos
api.timeout.seconds=30

# Logging de corpos HTTP na consola
api.logging.enabled=true
```

**Leitura da configuração:** os valores são lidos pela classe `ApiConfig` (presumivelmente em `config/ApiConfig.java`) e disponibilizados como constantes estáticas `ApiConfig.BASE_URL`, `ApiConfig.TIMEOUT` e `ApiConfig.LOGGING_ENABLED`.

**Implicação prática:** em ambiente de produção, `api.logging.enabled` deve ser definido como `false` para não imprimir tokens JWT e dados sensíveis na consola.

---

## 18. Fluxos de Dados Ponta a Ponta

### 18.1 Fluxo de Login Completo

```
Utilizador → escreve email + password → clica "Entrar"
    │
    ▼
PaginaLogin.handleLogin()
    │  authService.login(email, password, callback)
    ▼
AuthService.login()
    │  ApiQuery.execute(api().login(new LoginRequest(email, password)), callback)
    ▼
[OkHttp thread] POST http://localhost:8081/auth/login  {"email":"...","password":"..."}
    │
    ▼
[OkHttp thread] resposta 200 OK → body: {"id":"...","nome":"...","role":"GESTOR","token":"eyJ..."}
    │  ApiQuery: Platform.runLater(callback(QueryState.success(loginResponse)))
    ▼
[JavaFX thread] PaginaLogin callback: estado SUCCESS
    │  SessionManager.setUserId() / setUserName() / setUserEmail() / setUserRole() / setAuthToken()
    │  NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardGestor.fxml")
    ▼
DashboardGestor carregado; Sidebar.initialize() → applyRoleBasedVisibility()
    │  SessionManager.getUserRole() → "GESTOR"
    │  Esconde btnUtilizadores, btnEmpresas
    ▼
Ecrã principal com sidebar personalizada para GESTOR
```

### 18.2 Fluxo de Carregamento de Lista (Fornecedores)

```
FornecedoresController.initialize()
    │  carregarFornecedores()
    ▼
FornecedorService.getAll(0, 10, callback)
    │  ApiQuery.execute(api().findAll(0, 10), callback)
    │
    │  callback imediato: QueryState.loading()
    ▼
handleState(LOADING)
    │  setLoading(true) — overlay visível, botões desactivados
    │
    │  [OkHttp thread] GET http://localhost:8081/fornecedor?page=0&size=10
    │  Authorization: Bearer eyJ...
    │
    │  [OkHttp thread] resposta 200 OK → Gson desserializa PaginatedResponse<FornecedorResponse>
    │  Platform.runLater(callback(QueryState.success(response)))
    ▼
handleState(SUCCESS)
    │  setLoading(false)
    │  todosItens = response.content
    │  totalPages = response.totalPages
    │  actualizar lblPagina, btnAnterior, btnProxima
    │  renderizarTabela()
    ▼
renderizarTabela()
    │  tabelaContainer.getChildren().clear()
    │  criarLinhaHeader() → adicionar ao VBox
    │  para cada FornecedorResponse: criarLinhaTabela() → adicionar ao VBox
    ▼
Tabela visível para o utilizador
```

### 18.3 Fluxo de Criação (Modal)

```
Utilizador → clica "Criar Fornecedor"
    │
    ▼
FornecedoresController.handleNovo()
    │  CriarFornecedorModalController.show(service, window, this::onMutacaoBemSucedida)
    ▼
CriarFornecedorModalController.show()
    │  FXMLLoader.load() → nova Stage com Modality.APPLICATION_MODAL
    │  stage.showAndWait()
    │
[utilizador preenche formulário e clica "Criar"]
    │
    ▼
CriarFornecedorModalController.handleCriar()
    │  validar campos
    │  service.create(request, callback)
    │    [LOADING]: btnCriar.setDisable(true), btnCriar.setText("A criar...")
    │    [SUCCESS]: dialogStage.close(); onSuccess.accept("Fornecedor criado.")
    │    [ERROR]:   btnCriar.setDisable(false); lblErro.setText("Erro: ...")
    │
    │  (em caso de SUCCESS)
    ▼
stage.showAndWait() retorna
FornecedoresController.onMutacaoBemSucedida("Fornecedor criado.")
    │  MessageHelper.mostrar(rootStack, mensagem, true) → toast verde
    │  carregarFornecedores() → recarga da lista
    ▼
Lista actualizada com o novo fornecedor
```

---

## 19. Padrões Reutilizáveis Transversais

Esta secção resume os padrões técnicos que se repetem uniformemente em toda a aplicação, adequados para serem mencionados no relatório como elementos arquitecturais conscientemente adoptados.

### Padrão 1 — `setVisible(false)` + `setManaged(false)` para ocultar elementos

Usado em toda a aplicação para esconder nós da UI:
- `setVisible(false)`: oculta o nó visualmente.
- `setManaged(false)`: remove o nó do cálculo de layout (não ocupa espaço).

Apenas usar `setVisible(false)` sem `setManaged(false)` deixaria o espaço reservado para o nó.

### Padrão 2 — Reset de página ao filtrar

```java
// Consistente em todos os controllers
cbFiltroEstado.valueProperty().addListener((obs, old, val) -> {
    currentPage = 0;   // ← sempre reset à primeira página
    carregarDados();
});
```

### Padrão 3 — Verificação de nulos nos campos FXML

```java
// Padrão defensivo — todos os controlos opcionais são verificados
if (btnNovo != null) btnNovo.setDisable(loading);
if (fab != null)     fab.setDisable(loading);
if (lblPagina != null) lblPagina.setText(...);
```

Isto permite reutilizar o mesmo controller em layouts FXML ligeiramente diferentes sem `NullPointerException`.

### Padrão 4 — `Math.max(1, totalPages)` para evitar "Página 1 de 0"

```java
lblPagina.setText("Página " + (currentPage + 1) + " de " + Math.max(1, totalPages));
```

Quando a lista está vazia, `totalPages == 0`. O `Math.max(1, ...)` evita exibir "Página 1 de 0".

### Padrão 5 — Normalização de texto antes de exibir mensagens de erro

```java
// Em MessageHelper.java, linha 64
String textoLimpo = descricao.replaceAll("\\R", " ").strip();

// Em CriarEmpresaModalController.java, linha 123
lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
```

`\\R` é o padrão regex para qualquer terminador de linha (Unix, Windows, Mac). Garante que mensagens de erro multi-linha ficam numa única linha.

### Padrão 6 — `setManaged()` para controlo de layout de gráficos e spinners

```java
// Em DashboardFuncionarioMp.java, linhas 126–136
loadingPieChart.setVisible(true);
loadingPieChart.setManaged(true);
pieChartMaterias.setVisible(false);
pieChartMaterias.setManaged(false);

// Após carregar:
loadingPieChart.setVisible(false);
loadingPieChart.setManaged(false);
pieChartMaterias.setVisible(true);
pieChartMaterias.setManaged(true);
```

Spinner e gráfico ocupam o mesmo espaço e trocam de visibilidade/gestão.

### Padrão 7 — Uso de `List.of()` como valor padrão seguro

```java
private List<FornecedorResponse> todosItens = List.of();   // imutável, nunca null
// ...
todosItens = response.content != null ? response.content : List.of();
```

Evita `NullPointerException` ao iterar e garante que `todosItens` nunca é `null`.

### Padrão 8 — Callbacks com referência a método

```java
// Passagem de callbacks por referência a método (method reference)
service.create(request, this::onMutacaoBemSucedida);
CriarFornecedorModalController.show(service, window, this::onMutacaoBemSucedida);
```

`this::onMutacaoBemSucedida` é equivalente a `msg -> this.onMutacaoBemSucedida(msg)`. Este estilo é consistente em todo o projecto.

### Padrão 9 — `HBox.setHgrow(nó, Priority.ALWAYS)` para colunas elásticas

```java
nomeLabel.setMaxWidth(Double.MAX_VALUE);
HBox.setHgrow(nomeLabel, Priority.ALWAYS);
```

A coluna do nome expande para preencher todo o espaço disponível após as colunas de largura fixa.

### Padrão 10 — Tamanho de página configurável com `ComboBox<Integer>`

Todos os controllers de lista têm um `ComboBox<Integer>` com os valores `[5, 10, 20, 50, 100]` para o utilizador escolher quantos itens ver por página. O default varia por módulo (10 para listas de dados, 20 para catálogos simples como moedas e certificações).

---

*Fim do documento de análise técnica.*

*Ficheiro gerado a partir da leitura directa de todos os ficheiros fonte do projecto em:*
`c:\Users\tomep\PROJETOS\04_ESCOLA\LicenciaturaAno1\Projeto2\desktop_app\`
