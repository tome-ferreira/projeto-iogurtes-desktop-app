# Arquitectura — Gestão de Iogurtes (Aplicação Desktop)

Aplicação desktop JavaFX 21 + AtlantaFX com estrutura FXML + Controllers. Toda a UI estática está declarada em FXML; os controllers gerem apenas lógica dinâmica e chamadas HTTP.

---

## Estrutura do Projecto

```
desktop_app/
├── pom.xml                              # Maven (JavaFX 21, AtlantaFX, Retrofit2, OkHttp, Ikonli)
├── docs/
│   ├── ARCHITECTURE.md                  # ← este ficheiro
│   ├── API-CLIENT.md                    # Documentação do cliente HTTP assíncrono
│   └── CRUD-REFERENCE.md               # Referência de implementação CRUD
└── src/main/
    ├── resources/
    │   ├── config.properties            # URL base, timeout, logging HTTP
    │   ├── fxml/
    │   │   ├── layout/
    │   │   │   └── Sidebar.fxml         # Estrutura estática da barra lateral
    │   │   ├── paginas/
    │   │   │   ├── PaginaLogin.fxml
    │   │   │   ├── Dashboard.fxml
    │   │   │   └── Empresas.fxml
    │   │   └── components/
    │   │       └── empresas/
    │   │           ├── CriarEmpresaModal.fxml
    │   │           ├── EditarEmpresaModal.fxml
    │   │           └── EliminarEmpresaModal.fxml
    │   └── styles/
    │       ├── sidebar.css
    │       └── empresas.css
    └── java/com/gestaoiogurtes/
        │
        ├── GestaoIogurtes.java          # Ponto de entrada da aplicação, stage e navegação
        │
        ├── config/
        │   └── ApiConfig.java           # Lê config.properties para constantes estáticas
        │
        ├── api/
        │   ├── ApiQuery.java            # Executor assíncrono + Platform.runLater()
        │   ├── QueryState.java          # Estado imutável do pedido HTTP
        │   ├── RetrofitClient.java      # Singleton Retrofit + OkHttp
        │   └── services/
        │       └── IEmpresaApiService.java   # Interface Retrofit para o endpoint /empresas
        │
        ├── models/                      # DTOs (sem dependências JavaFX)
        │   └── empresa/
        │       ├── EmpresaResponse.java
        │       ├── CreateEmpresaRequest.java
        │       └── UpdateEmpresaRequest.java
        │
        ├── services/
        │   └── EmpresaService.java      # Serviço da aplicação (chamadas assíncronas via ApiQuery)
        │
        ├── layout/
        │   └── Sidebar.java             # Controller do Sidebar.fxml (navegação + temas)
        │
        ├── controllers/
        │   ├── PaginaLogin.java
        │   ├── Dashboard.java
        │   └── EmpresasController.java  # Controller da página Empresas (referência CRUD)
        │
        ├── components/
        │   └── empresas/
        │       ├── CriarEmpresaModalController.java
        │       ├── EditarEmpresaModalController.java
        │       └── EliminarEmpresaModalController.java
        │
        └── utils/
            ├── AppAware.java            # Interface para injecção do GestaoIogurtes
            ├── NavigationHelper.java    # Navegação entre páginas via FXML
            └── MessageHelper.java       # Mensagens de feedback AtlantaFX (Message)
```

| Pasta | Propósito |
|---|---|
| `config/` | Configuração estática lida de `config.properties` |
| `api/` | Infraestrutura Retrofit: singleton, estado, executor assíncrono |
| `api/services/` | Interfaces Retrofit (uma por recurso da API REST) |
| `models/<domain>/` | Objectos de transferência de dados (request/response). Sem imports JavaFX. |
| `services/` | Serviços da aplicação que delegam chamadas HTTP ao `ApiQuery` |
| `layout/` | Wrappers estruturais partilhados por todas as páginas |
| `controllers/` | Um controller por página navegável |
| `components/<feature>/` | Componentes de UI autocontidos (modais) |
| `utils/` | Utilitários partilhados sem estado |

---

## Como Criar uma Nova Página CRUD

### 1 — Criar o FXML da página

Criar `src/main/resources/fxml/paginas/MinhaEntidade.fxml` com um `BorderPane` raiz que inclua o Sidebar e um `StackPane` central (ver `Empresas.fxml` como referência).

### 2 — Criar o controller

Criar `src/main/java/com/gestaoiogurtes/controllers/MinhaEntidadeController.java` que implemente `AppAware`.

### 3 — Criar a interface Retrofit

Criar `src/main/java/com/gestaoiogurtes/api/services/IMinhaEntidadeApiService.java` com as anotações `@GET`, `@POST`, `@PUT`, `@DELETE` do Retrofit.

### 4 — Criar o serviço

Criar `src/main/java/com/gestaoiogurtes/services/MinhaEntidadeService.java` que:
1. Obtém o proxy Retrofit via `RetrofitClient.getInstance().getService(IMinhaEntidadeApiService.class)`
2. Delega cada operação ao `ApiQuery.execute(call, onStateChange)`

### 5 — Injectar o serviço no controller

```java
private final MinhaEntidadeService service = new MinhaEntidadeService();
```

### 6 — Registar navegação no Sidebar

Em `Sidebar.fxml`: adicionar um `<Button onAction="#handleMinhaEntidade">`.
Em `Sidebar.java`: adicionar o método `handleMinhaEntidade()` com `NavigationHelper.navigateTo(...)`.

---

## Padrão de Comunicação HTTP (ApiQuery)

Ver `docs/API-CLIENT.md` para a documentação completa.

Resumo do fluxo:

```
Controller
    │
    │  service.getAll(onStateChange)
    ▼
XxxService
    │  ApiQuery.execute(call, onStateChange)
    ▼
ApiQuery
    ├── onStateChange( QueryState.loading() )     ← imediato, na thread do controller
    │
    │  call.enqueue(...)                          ← background (OkHttp thread pool)
    │
    ├── [OK]   Platform.runLater( onStateChange( QueryState.success(body) ) )
    └── [Erro] Platform.runLater( onStateChange( QueryState.error(...) ) )
```

> **Regra fundamental:** `Platform.runLater()` é chamado **apenas** dentro de `ApiQuery`.
> Os controllers e serviços nunca tocam na thread JavaFX directamente.

---

## Convenções de Nomenclatura

| Artefacto | Convenção | Exemplo |
|---|---|---|
| Página FXML | `PascalCase.fxml` | `Empresas.fxml` |
| Controller de página | `<Domínio>Controller` | `EmpresasController` |
| Modal FXML | `<Acção><Domínio>Modal.fxml` | `CriarEmpresaModal.fxml` |
| Controller de modal | `<Acção><Domínio>ModalController` | `CriarEmpresaModalController` |
| Interface Retrofit | `I<Domínio>ApiService` | `IEmpresaApiService` (em `api/services/`) |
| Serviço | `<Domínio>Service` | `EmpresaService` (em `services/`) |
| Response DTO | `<Domínio>Response` | `EmpresaResponse` |
| Request DTO | `<Acção><Domínio>Request` | `CreateEmpresaRequest` |

---

## Mensagens de Feedback ao Utilizador

Usar sempre `MessageHelper.mostrar(rootStack, mensagem, sucesso)`.

```java
MessageHelper.mostrar(rootStack, "Empresa criada com sucesso!", true);  // verde
MessageHelper.mostrar(rootStack, "Erro: " + mensagem,         false); // vermelho
```

O `MessageHelper` usa o componente `atlantafx.base.controls.Message` (não `Notification`).
Ver `docs/CRUD-REFERENCE.md` para a documentação completa do padrão.
