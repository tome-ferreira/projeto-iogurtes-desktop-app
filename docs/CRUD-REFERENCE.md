# CRUD-REFERENCE.md — Guia de Referência para Páginas CRUD

> **Este ficheiro é o modelo de referência para todas as páginas CRUD futuras.**
> A implementação da página **Empresas** é o exemplo canónico.
> Para criar uma nova página CRUD, segue os passos descritos aqui sem precisar
> de consultar o código das Empresas.

---

## Visão Geral

### Stack utilizada

| Camada              | Tecnologia                                |
|---------------------|-------------------------------------------|
| HTTP Client         | Retrofit 2 + OkHttp 4 + Gson converter   |
| Execução assíncrona | `ApiQuery.execute()` (ReactQuery-inspired) |
| Estado do pedido    | `QueryState<T>` (IDLE → LOADING → SUCCESS\|ERROR) |
| UI Framework        | JavaFX 21 + AtlantaFX 2.0.1              |
| Ícones              | Ikonli + Material Design 2               |
| CSS                 | Variáveis AtlantaFX (`-color-*`)         |

### Fluxo de dados

```
Controller
    │
    │  service.getAll(state -> { ... })
    ▼
XxxService
    │  RetrofitClient.getInstance().getService(IXxxApiService.class)
    │  ApiQuery.execute(call, onStateChange)
    ▼
ApiQuery
    │──► onStateChange(QueryState.loading())   ← imediato, na thread do controller
    │
    │   call.enqueue(...)   ← thread OkHttp (background)
    │
    ├── [resposta OK]  ──► Platform.runLater(onStateChange(QueryState.success(body)))
    │
    └── [falha / erro] ──► Platform.runLater(onStateChange(QueryState.error(...)))
```

> **Regra fundamental:** `Platform.runLater()` é chamado **exclusivamente** dentro
> de `ApiQuery`. Os controllers **nunca** o invocam directamente.

---

## Estrutura de ficheiros de uma página CRUD

Para um recurso chamado `Xxx`, os ficheiros são:

```
src/
├── main/
│   ├── java/com/gestaoiogurtes/
│   │   ├── api/services/
│   │   │   └── IXxxApiService.java          ← interface Retrofit com @GET/@POST/@PUT/@DELETE
│   │   ├── models/
│   │   │   └── xxx/
│   │   │       ├── XxxResponse.java             ← DTO de resposta (campos = chaves JSON)
│   │   │       ├── CreateXxxRequest.java        ← corpo do POST
│   │   │       └── UpdateXxxRequest.java        ← corpo do PUT
│   │   ├── services/
│   │   │   └── XxxService.java              ← serviço da aplicação: RetrofitClient + ApiQuery
│   │   ├── controllers/
│   │   │   └── XxxController.java           ← controller da página
│   │   └── components/xxx/
│   │       ├── CriarXxxModalController.java
│   │       ├── EditarXxxModalController.java
│   │       └── EliminarXxxModalController.java
│   └── resources/
│       ├── fxml/
│       │   └── paginas/
│       │       └── Xxx.fxml                 ← layout da página
│       └── styles/
│           └── xxx.css                      ← estilos da página (zero inline styles)
docs/
└── CRUD-REFERENCE.md                        ← este ficheiro
```

**Papel de cada ficheiro:**

| Ficheiro | Papel |
|----------|-------|
| `IXxxApiService.java` (api/services) | Mapeamento Retrofit dos endpoints HTTP |
| `XxxResponse.java` | Modelo da resposta JSON (campos = chaves JSON para Gson) |
| `CreateXxxRequest.java` | Corpo do POST (campos = chaves JSON) |
| `UpdateXxxRequest.java` | Corpo do PUT (campos = chaves JSON) |
| `XxxService.java` | Serviço da aplicação: delega chamadas HTTP ao Retrofit via ApiQuery |
| `XxxController.java` | Lógica da página: tabela, filtros, loading, notificações |
| `Xxx.fxml` | Estrutura estática: cabeçalho, toolbar, StackPane raiz, overlay |
| `CriarXxxModalController.java` | Modal de criação com formulário |
| `EditarXxxModalController.java` | Modal de edição pré-preenchido |
| `EliminarXxxModalController.java` | Modal de confirmação de eliminação |
| `xxx.css` | Todos os estilos da página — zero inline |

---

## Passo a passo para criar uma nova página CRUD

### 1. Ir buscar a documentação da API

```
GET http://localhost:8081/v3/api-docs
```

Identificar a tag do recurso (ex: `empresa-controller`) e extrair:
- Paths e métodos HTTP (`GET /xxx`, `POST /xxx`, `PUT /xxx/{id}`, `DELETE /xxx/{id}`)
- Schemas de resposta (`XxxResponse`) e de pedido (`CreateXxxRequest`, `UpdateXxxRequest`)

---

### 2. Criar `IXxxApiService.java` — mapeamento dos endpoints

```java
// src/main/java/com/gestaoiogurtes/api/services/IXxxApiService.java
package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.xxx.XxxResponse;
import com.gestaoiogurtes.models.xxx.CreateXxxRequest;
import com.gestaoiogurtes.models.xxx.UpdateXxxRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface IXxxApiService {

    @GET("xxx")
    Call<List<XxxResponse>> findAll();

    @GET("xxx/{id}")
    Call<XxxResponse> findById(@Path("id") String id);

    @POST("xxx")
    Call<XxxResponse> create(@Body CreateXxxRequest request);

    @PUT("xxx/{id}")
    Call<XxxResponse> update(@Path("id") String id, @Body UpdateXxxRequest request);

    @DELETE("xxx/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
```

> **Regra:** os paths não devem começar com `/`. O Retrofit concatena-os à `BASE_URL`.

---

### 3. Criar os modelos

```java
// XxxResponse.java
package com.gestaoiogurtes.models.xxx;

import java.time.LocalDateTime;
import java.util.UUID;

public class XxxResponse {
    public UUID          id;
    public String        campo1;    // nomes = chaves JSON exactas
    public String        campo2;
    public LocalDateTime createdAt;

    public XxxResponse() {} // necessário para Gson
}
```

> **Regra:** os nomes dos campos devem coincidir **exactamente** com as chaves JSON
> para que o `GsonConverterFactory` faça o mapeamento automaticamente.

---

### 4. Criar `XxxService.java`

```java
// src/main/java/com/gestaoiogurtes/services/XxxService.java
package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.models.xxx.*;
import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

public class XxxService {

    private com.gestaoiogurtes.api.services.IXxxApiService api() {
        return RetrofitClient.getInstance()
                .getService(com.gestaoiogurtes.api.services.IXxxApiService.class);
    }

    public void getAll(Consumer<QueryState<List<XxxResponse>>> cb) {
        ApiQuery.execute(api().findAll(), cb);
    }

    public void getById(String id, Consumer<QueryState<XxxResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateXxxRequest req, Consumer<QueryState<XxxResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateXxxRequest req, Consumer<QueryState<XxxResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }
}
```

---

### 5. Criar o controller e o FXML da página

O controller segue sempre a mesma estrutura. Ver `EmpresasController.java` como referência.

**Campos obrigatórios no controller:**

```java
@FXML private VBox       tabelaContainer; // container das linhas
@FXML private StackPane  rootStack;       // StackPane raiz (tabela + overlay)
@FXML private VBox       loadingOverlay;  // overlay de loading
@FXML private TextField  campoPesquisa;   // campo de pesquisa
@FXML private Button     btnNovo;         // botão "Novo" do cabeçalho
@FXML private Button     fab;             // botão flutuante
```

**Estrutura do StackPane no FXML (padrão de loading overlay):**

```xml
<StackPane fx:id="rootStack" VBox.vgrow="ALWAYS">

    <!-- [0] Wrapper da tabela -->
    <VBox VBox.vgrow="ALWAYS">
        <padding><Insets top="0" right="32" bottom="32" left="32"/></padding>
        <VBox fx:id="tabelaContainer" styleClass="tabela-card"
              fillWidth="true" VBox.vgrow="ALWAYS"/>
    </VBox>

    <!-- [1] Overlay de loading — visível apenas durante LOADING -->
    <VBox fx:id="loadingOverlay" styleClass="loading-overlay"
          alignment="CENTER" visible="false" managed="false">
        <ProgressIndicator styleClass="loading-spinner"/>
        <Label text="A carregar..." styleClass="loading-label"/>
    </VBox>

    <!-- FAB -->
    <Button fx:id="fab" styleClass="fab" onAction="#handleNovo"
            StackPane.alignment="BOTTOM_RIGHT">
        <graphic>
            <FontIcon iconLiteral="mdi2p-plus" iconSize="22" styleClass="fab-icone"/>
        </graphic>
        <tooltip><Tooltip text="Nova entrada"/></tooltip>
        <StackPane.margin>
            <Insets top="0" right="24" bottom="24" left="0"/>
        </StackPane.margin>
    </Button>

</StackPane>
```

---

### 6. Criar os modais (Criar, Editar, Eliminar, Detalhes)

Cada modal é composto por dois ficheiros: um **FXML** que define o layout e
um **controller Java** com um método estático `show(...)`.

O método estático segue sempre o mesmo padrão:

```java
// Exemplo genérico
public static void show(XxxResponse entidade, XxxService service, Window owner, Consumer<String> onSuccess) {
    FXMLLoader loader = new FXMLLoader(
            XxxModalController.class.getResource("/fxml/components/xxx/XxxModal.fxml"));
    Parent root = loader.load();

    Stage stage = new Stage();
    stage.initOwner(owner);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.UTILITY);
    stage.setResizable(false);
    stage.setTitle("Título do Modal");
    stage.setScene(new Scene(root));

    XxxModalController ctrl = loader.getController();
    ctrl.dialogStage = stage;
    ctrl.onSuccess   = onSuccess;
    ctrl.service     = service;
    ctrl.setEntidade(entidade); // pré-preenchimento (modal de Editar/Detalhes)

    stage.showAndWait();
}
```

**Ficheiros de referência (Empresas):**
- `CriarEmpresaModalController.java` + `CriarEmpresaModal.fxml`
- `EditarEmpresaModalController.java` + `EditarEmpresaModal.fxml`
- `EliminarEmpresaModalController.java` + `EliminarEmpresaModal.fxml`

---

### 7. Criar `xxx.css`

Copiar `empresas.css` e adaptar. A secção 8 (loading overlay) é sempre idêntica
— copiar sem alterações.

---

### 8. Adicionar à sidebar

**`Sidebar.java`:**
```java
@FXML
private void handleXxx() {
    NavigationHelper.navigateTo(app, "/fxml/paginas/Xxx.fxml");
}
```

**`Sidebar.fxml`** (copiar o padrão dos existentes):
```xml
<Button fx:id="btnXxx" styleClass="sidebar-item"
        maxWidth="Infinity" alignment="CENTER_LEFT"
        contentDisplay="GRAPHIC_ONLY" onAction="#handleXxx">
    <graphic>
        <HBox spacing="10" alignment="CENTER_LEFT">
            <FontIcon iconLiteral="mdi2x-xxx-outline" iconSize="18"/>
            <Label text="Xxx" maxWidth="Infinity" HBox.hgrow="ALWAYS"/>
        </HBox>
    </graphic>
    <tooltip><Tooltip text="Gestão de Xxx"/></tooltip>
    <padding><Insets top="9" right="12" bottom="9" left="10"/></padding>
</Button>
```

---

## Padrão de loading state

### Como mostrar/esconder o spinner

O overlay de loading é um `VBox` com `styleClass="loading-overlay"` que é o segundo
filho do `StackPane` raiz. O controller activa/desactiva via:

```java
private void setLoading(boolean loading) {
    if (loadingOverlay != null) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setManaged(loading);
    }
    // Desactivar botões de acção para evitar double-submit
    if (btnNovo != null) btnNovo.setDisable(loading);
    if (fab     != null) fab.setDisable(loading);
}
```

### Código de exemplo completo

```java
private void carregarItens() {
    service.getAll(state -> {
        switch (state.getStatus()) {
            case LOADING -> setLoading(true);
            case SUCCESS -> {
                setLoading(false);
                todosItens = state.getData() != null ? state.getData() : List.of();
                renderizarTabela();
            }
            case ERROR -> {
                setLoading(false);
                mostrarNotificacao("Erro: " + state.getErrorMessage(), false);
            }
            default -> {} // IDLE — ignorar
        }
    });
}
```

---

## Padrão de Mensagens de Feedback (AtlantaFX Message)

> **Usar sempre `MessageHelper.mostrar()`** — nunca instanciar `Message` ou `Notification`
> directamente nos controllers.

### `MessageHelper` — utilitário partilhado

Localização: `src/main/java/com/gestaoiogurtes/utils/MessageHelper.java`

```java
// No controller da página (ex.: EmpresasController, FornecedoresController, …):
MessageHelper.mostrar(rootStack, "Empresa criada com sucesso!", true);  // verde
MessageHelper.mostrar(rootStack, "Erro: " + mensagem,         false); // vermelho
```

O helper faz internamente:
1. **Limpa newlines** da mensagem (substitui `\n`, `\r\n`, etc. por espaço)
2. **Usa `Message(título, descrição, ícone)`** — o componente `Message` tem `title` + `description`, ao contrário do `Notification` que tinha apenas `message`. O título é "Sucesso" / "Erro" automaticamente.
3. **Define `setOnClose`** para que o botão ✕ apareça (documentado na API oficial)
4. **Auto-fecha** após 4 segundos via `PauseTransition`
5. **Posiciona** no topo direito via `StackPane.setAlignment` + `StackPane.setMargin`

### Padrão no controller (callback de mutação)

```java
// No EmpresasController (ou qualquer controller CRUD):
private void mostrarNotificacao(String mensagem, boolean sucesso) {
    MessageHelper.mostrar(rootStack, mensagem, sucesso);
}

public void onMutacaoBemSucedida(String mensagem) {
    mostrarNotificacao(mensagem, true);
    carregarXxx();
}

public void onMutacaoComErro(String mensagem) {
    mostrarNotificacao(mensagem, false);
}
```

### Pré-requisito: `rootStack` deve ser o StackPane raiz

```xml
<!-- Empresas.fxml — o StackPane raiz com fx:id="rootStack" -->
<StackPane fx:id="rootStack" VBox.vgrow="ALWAYS">
    <!-- ... tabela, overlay, FAB ... -->
</StackPane>
```

### API do AtlantaFX `Message`

| Propriedade | Tipo | Descrição |
|-------------|------|-----------|
| `title` | `String` | Título curto (ex: "Sucesso", "Erro") |
| `description` | `String` | Texto descritivo da mensagem |
| `graphic` | `Node` | Ícone (ex: `FontIcon`) |
| `onClose` | `EventHandler` | Handler do botão ✕ (sem handler → botão não aparece) |

```java
// Construtor completo:
new Message(String title, String description, Node graphic)
```



## Padrão ApiQuery num controller

### Exemplo de createAll com loading + success + error

```java
service.create(request, state -> {
    if (state.isLoading()) {
        nodeBtnCriar.setDisable(true);
        nodeBtnCriar.setText("A criar...");
    } else if (state.isSuccess()) {
        dialog.close();
        onSucesso.accept("Item criado com sucesso.");
    } else if (state.isError()) {
        nodeBtnCriar.setDisable(false);
        nodeBtnCriar.setText("Criar");
        lblErro.setText("Erro: " + state.getErrorMessage());
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }
});
```

> **Nota sobre Tratamento de Erros:** O `ApiQuery` extrai automaticamente o campo `"message"`
> de respostas de erro JSON (ex: HTTP 4xx/5xx). O valor estará disponível através do
> `state.getErrorMessage()`.

---

## Paginação

### Quando usar
Sempre — todos os endpoints de listagem suportam paginação. Este é o padrão obrigatório para todas as páginas CRUD.

### PaginatedResponse<T>
Existe um modelo genérico `src/main/java/com/gestaoiogurtes/models/PaginatedResponse.java` que representa o wrapper retornado pela API.
- `content`: Os dados da página atual
- `totalElements`: Total de elementos em todas as páginas
- `totalPages`: Número total de páginas
- `currentPage`: Índice da página atual (0-based)
- `pageSize`: Tamanho da página solicitado
- `first`: Indica se é a primeira página
- `last`: Indica se é a última página

### Passos para implementar paginação numa nova página
1. Atualizar o método da API (interface) para aceitar os parâmetros `@Query` de paginação:
   `Call<PaginatedResponse<XxxResponse>> findAll(@Query("page") int page, @Query("size") int size);`
2. Atualizar a assinatura do serviço para receber a página e tamanho.
3. Adicionar o estado ao controller: `currentPage`, `pageSize`, `totalPages`.
4. Adicionar a barra de paginação ao FXML (`HBox` com botão Anterior, label, ComboBox de tamanho de página, e botão Próxima).
   ```xml
   <!-- Barra de paginação -->
   <HBox styleClass="pagination-bar" alignment="CENTER_LEFT" spacing="8">
       <Label fx:id="lblPagina" text="Página 1 de 1" styleClass="pagination-label"/>
       <Region HBox.hgrow="ALWAYS"/>
       <HBox alignment="CENTER" spacing="8">
           <Button fx:id="btnAnterior" text="Anterior" onAction="#handlePaginaAnterior" styleClass="btn-pagination">
               <graphic><FontIcon iconLiteral="mdi2c-chevron-left" iconSize="18"/></graphic>
           </Button>
           <Label text="Linhas por página:" styleClass="pagination-label"/>
           <ComboBox fx:id="cbTamanhoPagina" styleClass="pagination-combo"/>
           <Button fx:id="btnProxima" text="Próxima" contentDisplay="RIGHT" onAction="#handleProximaPagina" styleClass="btn-pagination">
               <graphic><FontIcon iconLiteral="mdi2c-chevron-right" iconSize="18"/></graphic>
           </Button>
       </HBox>
   </HBox>
   ```
5. Criar `handleProximaPagina()` e `handlePaginaAnterior()` que incrementam/decrementam `currentPage` e chamam o reload.
6. Atualizar a desativação dos botões baseada em `response.first` e `response.last`.
7. Após Create/Update/Delete, invocar o método de carregamento da página atual para não perder o contexto da tabela (ou seja, `carregarXxx()` mantém-se na mesma `currentPage`).

### Código de exemplo (Controller)
```java
// Estado
private int currentPage = 0;
private int pageSize = 20;
private int totalPages = 0;

@FXML private ComboBox<Integer> cbTamanhoPagina;

@FXML
public void initialize() {
    if (cbTamanhoPagina != null) {
        cbTamanhoPagina.getItems().addAll(10, 20, 50, 100);
        cbTamanhoPagina.setValue(pageSize);
        cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
            if (val != null && val != pageSize) {
                pageSize = val;
                currentPage = 0;
                carregarItens();
            }
        });
    }
    carregarItens();
}

private void carregarItens() {
    service.getAll(currentPage, pageSize, state -> {
        switch (state.getStatus()) {
            case LOADING -> setLoading(true);
            case SUCCESS -> {
                setLoading(false);
                var response = state.getData();
                if (response != null) {
                    todosItens = response.content != null ? response.content : List.of();
                    this.totalPages = response.totalPages;
                    // Nota: NÃO atualizamos this.currentPage com response.currentPage
                    // porque a nossa classe de Controller já gere a página actual com sucesso.
                    
                    if (lblPagina != null) {
                        lblPagina.setText("Página " + (this.currentPage + 1) + " de " + Math.max(1, this.totalPages));
                    }
                    if (btnAnterior != null) btnAnterior.setDisable(response.first);
                    if (btnProxima != null) btnProxima.setDisable(response.last);
                }
                renderizarTabela();
            }
            case ERROR -> {
                setLoading(false);
                mostrarNotificacao("Erro: " + state.getErrorMessage(), false);
            }
            default -> {}
        }
    });
}

@FXML
private void handlePaginaAnterior() {
    if (currentPage > 0) {
        currentPage--;
        carregarItens();
    }
}

@FXML
private void handleProximaPagina() {
    if (currentPage < totalPages - 1) {
        currentPage++;
        carregarItens();
    }
}
```

---

## Convenções obrigatórias

### Nomes de ficheiros e classes

| Tipo | Convenção | Exemplo |
|------|-----------|---------|
| Interface Retrofit | `IXxxApiService` em `api/services/` | `IEmpresaApiService` |
| Modelo resposta | `XxxResponse` em `models/xxx/` | `EmpresaResponse` |
| Modelo criar | `CreateXxxRequest` em `models/xxx/` | `CreateEmpresaRequest` |
| Modelo actualizar | `UpdateXxxRequest` em `models/xxx/` | `UpdateEmpresaRequest` |
| Serviço | `XxxService` em `services/` | `EmpresaService` |
| Controller página | `XxxController` em `controllers/` | `EmpresasController` |
| FXML página | `Xxx.fxml` em `fxml/paginas/` | `Empresas.fxml` |
| Modal criar | `CriarXxxModalController` em `components/xxx/` | `CriarEmpresaModalController` |
| Modal editar | `EditarXxxModalController` | `EditarEmpresaModalController` |
| Modal eliminar | `EliminarXxxModalController` | `EliminarEmpresaModalController` |
| CSS | `xxx.css` em `styles/` | `empresas.css` |

### Zero inline styles

```java
// ERRADO:
label.setStyle("-fx-font-size: 14px;");

// CORRECTO:
label.getStyleClass().add("celula-nome-principal");
```

### `Platform.runLater()` nunca nos controllers

O `ApiQuery` garante que todos os callbacks chegam na JavaFX Application Thread.
Os controllers podem actualizar a UI directamente nos callbacks sem usar `Platform.runLater()`.

### Campos dos modelos = chaves JSON exactas

Os nomes dos campos devem coincidir exactamente com as chaves JSON.
O `GsonConverterFactory` faz o mapeamento automaticamente por nome.

### Os serviços são instanciados directamente

Os serviços assíncronos (que usam `ApiQuery`) podem ser instanciados com `new XxxService()`.
São sempre conectados ao backend e não precisam de injecção de dependências complexa.

### Padrão opcional: Modal de Detalhes

**Quando usar:**
- Quando uma entidade tem campos que são demasiado extensos para a listagem
- Quando a listagem deve mostrar apenas os campos mais importantes
- Quando um campo é uma FK para outra entidade e o detalhe precisa de resolver o nome legível

**Como implementar:**
- Remover colunas verbosas da TableView/tabela customizada
- Criar `DetalhesXxxModal.fxml` + `DetalhesXxxModalController.java`
- Ligar o clique na linha ou um botão "Detalhes" para abrir o modal
- Apresentar todos os campos em modo leitura com `TextField editable="false"` e `styleClass="form-control"`
- Para FKs (ex: `empresaId`): resolver o nome via `GET /entidade/{id}` no `setDados()` e apresentar o nome, não o UUID
- Ver exemplo completo na secção [Padrão de Relações entre Objetos](#padrão-de-relações-entre-objetos)

**Código de exemplo:**

```java
// DetalhesClienteModalController.java
public void preencherDados(UserResponse u, boolean inativo, EmpresaService empresaService) {
    txtNome.setText(u.nome != null ? u.nome : "—");
    txtEmail.setText(u.email != null ? u.email : "—");

    if (u.empresaId != null && !u.empresaId.isBlank()) {
        loadingEmpresa.setVisible(true);
        lblEmpresaNome.setText("A carregar...");

        empresaService.getById(u.empresaId, state -> {
            if (state.isSuccess() && state.getData() != null) {
                loadingEmpresa.setVisible(false);
                lblEmpresaNome.setText(state.getData().nomeEmpresa);
            } else {
                loadingEmpresa.setVisible(false);
                lblEmpresaNome.setText("Sem empresa associada");
            }
        });
    } else {
        lblEmpresaNome.setText("Sem empresa associada");
    }
}
```

---

## Padrão de Relações entre Objetos

> **Implementação de referência:** `SelecionarEmpresaModalController` +
> `EmpresaSelecao` em `src/main/java/com/gestaoiogurtes/components/utilizadores/`.

### Quando usar este padrão

Usar este padrão quando:
- Um campo do formulário é uma FK para outra entidade (ex: `empresaId` em Cliente)
- Um campo de texto livre (TextField com UUID) não é adequado para o utilizador
- É necessário deixar o utilizador escolher de uma lista paginada

### Componentes envolvidos

| Componente | Papel |
|---|---|
| `SelecionarXxxModal.fxml` + `SelecionarXxxModalController.java` | Modal de selecção com lista paginada e selecção única |
| `XxxSelecao.java` | POJO com os dois campos necessários: `id` e `nome` |
| Modal pai (Criar/Editar) | Guarda `selectedXxxId`; apresenta nome via `lblXxxNome` |
| Modal de Detalhes | Chama `GET /xxx/{id}` para resolver o nome a partir do FK |

### Passo a passo para implementar uma nova relação

#### 1. Criar `XxxSelecao.java`

```java
// src/main/java/com/gestaoiogurtes/components/yyy/XxxSelecao.java
public class XxxSelecao {
    public final String id;
    public final String nome;

    public XxxSelecao(String id, String nome) {
        this.id   = id;
        this.nome = nome;
    }
}
```

#### 2. Criar `SelecionarXxxModal.fxml` e `SelecionarXxxModalController.java`

O controller deve:
- Aceitar `Consumer<XxxSelecao> onConfirm` como parâmetro do `show()`
- Aceitar um `preSelectedId` opcional
- Carregar 10 itens por página via `XxxService.getAll(page, 10, callback)`
- Guardar `selectedId` e `selectedNome` em memória (persistem entre páginas)
- Marcar visualmente a linha seleccionada com um `RadioButton`
- Desactivar "Continuar" até haver uma selecção
- Chamar `onConfirm` **só** ao clicar "Continuar" — nunca ao fechar com X

Ver `SelecionarEmpresaModalController.java` como implementação de referência.

#### 3. No modal Criar — adicionar botão + label ao FXML e controller

**FXML (em vez do TextField da FK):**
```xml
<VBox styleClass="form-group" HBox.hgrow="ALWAYS">
    <Label text="Empresa*" styleClass="form-label"/>
    <!-- Nome apresentado após selecção (hidden por omissão) -->
    <Label fx:id="lblEmpresaNome" styleClass="empresa-selecionada-nome"
           visible="false" managed="false" wrapText="true"/>
    <!-- Botão que abre o picker -->
    <Button fx:id="btnSelecionarEmpresa" text="Selecionar Empresa"
            onAction="#handleSelecionarEmpresa"
            styleClass="btn-selecionar-empresa"/>
</VBox>
```

**Controller:**
```java
private String selectedEmpresaId;

@FXML
private void handleSelecionarEmpresa() {
    SelecionarEmpresaModalController.show(
            empresaService,
            dialogStage,
            null,   // sem pré-selecção no modal Criar
            selecao -> {
                selectedEmpresaId = selecao.id;
                lblEmpresaNome.setText(selecao.nome);
                lblEmpresaNome.setVisible(true);
                lblEmpresaNome.setManaged(true);
                btnSelecionarEmpresa.setText("Alterar Empresa");
            }
    );
}
```

#### 4. No modal Editar — pré-carregar o nome e passar pré-selecção

```java
private void setUtilizador(UserResponse u) {
    txtNome.setText(u.nome != null ? u.nome : "");

    if (u.empresaId != null && !u.empresaId.isBlank()) {
        selectedEmpresaId = u.empresaId;
        lblEmpresaNome.setText("A carregar...");
        lblEmpresaNome.setVisible(true);
        lblEmpresaNome.setManaged(true);
        btnSelecionarEmpresa.setText("Alterar Empresa");

        // Buscar o nome para mostrar imediatamente
        empresaService.getById(u.empresaId, state -> {
            if (state.isSuccess() && state.getData() != null) {
                lblEmpresaNome.setText(state.getData().nomeEmpresa);
            } else {
                lblEmpresaNome.setText(u.empresaId); // fallback: mostrar o UUID
            }
        });
    }
}

@FXML
private void handleSelecionarEmpresa() {
    SelecionarEmpresaModalController.show(
            empresaService,
            dialogStage,
            selectedEmpresaId,   // pré-selecção — o utilizador vê a empresa actual marcada
            selecao -> {
                selectedEmpresaId = selecao.id;
                lblEmpresaNome.setText(selecao.nome);
            }
    );
}
```

#### 5. No modal Detalhes — resolver o nome via API

```java
// preencherDados() — chamado após showAndWait no show()
if (u.empresaId != null && !u.empresaId.isBlank()) {
    loadingEmpresa.setVisible(true);
    loadingEmpresa.setManaged(true);
    lblEmpresaNome.setText("A carregar...");

    empresaService.getById(u.empresaId, state -> {
        loadingEmpresa.setVisible(false);
        loadingEmpresa.setManaged(false);
        if (state.isSuccess() && state.getData() != null) {
            lblEmpresaNome.setText(state.getData().nomeEmpresa);
        } else {
            lblEmpresaNome.setText("Sem empresa associada");
        }
    });
} else {
    lblEmpresaNome.setText("Sem empresa associada");
}
```

### Regras obrigatórias

| Regra | Detalhe |
|---|---|
| `onConfirm` só ao confirmar | Nunca chamado ao fechar com X ou ESC |
| Selecção persiste entre páginas | `selectedId` e `selectedNome` são campos do controller, não da linha |
| Estado de loading sempre visível | Spinner inline enquanto cada página carrega |
| Pré-selecção suportada | `preSelectedId` passado ao `show()`; a linha correspondente aparece marcada |
| Zero inline styles | Todos os estilos em `xxx.css` via variáveis CSS |
| `Platform.runLater()` só no ApiQuery | Os controllers nunca o chamam directamente |

---

*Referência criada com base na implementação da página Empresas.*
