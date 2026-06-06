# API-CLIENT.md — Cliente HTTP Assíncrono

> Documentação técnica em Português Europeu para o módulo de comunicação
> com a API REST do backend Spring Boot.

---

## Visão Geral

Esta infraestrutura implementa um padrão **ReactQuery-inspired** para execução assíncrona de pedidos HTTP dentro de uma aplicação JavaFX. O objectivo é separar claramente a lógica de rede da camada de apresentação, eliminando callbacks aninhados e tornando o fluxo de dados previsível.

### Os dois componentes principais

| Classe | Responsabilidade |
|--------|-----------------|
| `QueryState<T>` | Representa o **estado** de um pedido HTTP num dado momento. É imutável — cada transição cria uma nova instância. |
| `ApiQuery` | **Executa** um pedido Retrofit assincronamente e notifica o controller sobre cada transição de estado através de um simples `Consumer<QueryState<T>>`. |

### Fluxo completo

```
Controller JavaFX
      │
      │  ApiQuery.execute(call, onStateChange)
      ▼
  ApiQuery
      │──► onStateChange( QueryState.loading() )   ← imediato, na thread do controller
      │
      │  call.enqueue(...)   ← thread pool OkHttp (background)
      │
      ├── [resposta OK]  ──► Platform.runLater( onStateChange( QueryState.success(body) ) )
      │
      └── [falha / erro] ──► Platform.runLater( onStateChange( QueryState.error(...) ) )
```

> **Regra fundamental:** `Platform.runLater()` é chamado **exclusivamente** dentro de `ApiQuery`.
> Os controllers nunca interagem directamente com a thread do JavaFX.

---

## QueryState — Referência Rápida

### Estados possíveis

| Estado | Descrição | `data` | `errorMessage` / `error` |
|--------|-----------|--------|--------------------------|
| `IDLE` | Nenhum pedido iniciado | `null` | `null` |
| `LOADING` | Pedido em curso na rede | `null` | `null` |
| `SUCCESS` | Resposta recebida com sucesso | ✅ preenchido | `null` |
| `ERROR` | Pedido falhou (rede, HTTP 4xx/5xx) | `null` | ✅ preenchidos |

### Métodos de fábrica

```java
QueryState<T> idle    = QueryState.idle();
QueryState<T> loading = QueryState.loading();
QueryState<T> ok      = QueryState.success(body);
QueryState<T> falhou  = QueryState.error("Mensagem", excepção);
```

### Predicados de conveniência

```java
state.isLoading()  // → true quando Status == LOADING
state.isSuccess()  // → true quando Status == SUCCESS
state.isError()    // → true quando Status == ERROR
```

### Exemplo completo num controller JavaFX

```java
// ─── Num controller JavaFX (ex.: EmpresasController) ───────────────────────

@FXML private TableView<EmpresaResponse> tabela;
@FXML private ProgressIndicator         spinner;
@FXML private Label                     lblErro;

private void carregarEmpresas() {
    // 1. Instanciar o serviço
    var service = new EmpresaService();

    // 2. Executar via ApiQuery — tratar cada estado no mesmo lambda
    service.getAll(state -> {
        switch (state.getStatus()) {

            case LOADING -> {
                spinner.setVisible(true);
                lblErro.setVisible(false);
            }

            case SUCCESS -> {
                spinner.setVisible(false);
                tabela.getItems().setAll(state.getData());
            }

            case ERROR -> {
                spinner.setVisible(false);
                lblErro.setText("Erro: " + state.getErrorMessage());
                lblErro.setVisible(true);
            }

            default -> {} // IDLE — ignorar
        }
    });
}
```

> **Nota:** O `onStateChange` é chamado automaticamente na JavaFX Application Thread
> para as transições `SUCCESS` e `ERROR`. Podes actualizar a UI directamente.

---

## Como usar ApiQuery

### Passo 1 — Criar uma interface Retrofit

Cria um ficheiro em `src/main/java/com/gestaoiogurtes/api/services/`:

```java
package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.models.empresa.CreateEmpresaRequest;
import com.gestaoiogurtes.models.empresa.UpdateEmpresaRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface IEmpresaApiService {

    @GET("api/empresas")
    Call<List<EmpresaResponse>> findAll();

    @POST("api/empresas")
    Call<EmpresaResponse> create(@Body CreateEmpresaRequest request);

    @PUT("api/empresas/{id}")
    Call<EmpresaResponse> update(@Path("id") String id, @Body UpdateEmpresaRequest request);

    @DELETE("api/empresas/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
```

### Passo 2 — Obter a instância via RetrofitClient

```java
IEmpresaApiService api = RetrofitClient.getInstance()
                                       .getService(IEmpresaApiService.class);
```

### Passo 3 — Criar um Call e passar ao ApiQuery

```java
Call<List<EmpresaResponse>> call = api.findAll();

ApiQuery.execute(call, state -> {
    if (state.isLoading())  { /* mostrar spinner */ }
    if (state.isSuccess())  { /* usar state.getData() */ }
    if (state.isError())    { /* mostrar state.getErrorMessage() */ }
});
```

### Tratar cada estado no controller

| Estado | O que fazer no controller |
|--------|--------------------------|
| `LOADING` | Mostrar `ProgressIndicator`, desactivar botões de acção |
| `SUCCESS` | Ocultar spinner, popular `TableView` / `ListView` com `state.getData()` |
| `ERROR`   | Ocultar spinner, mostrar mensagem de erro ao utilizador (`state.getErrorMessage()`). O `ApiQuery` tenta extrair automaticamente o campo `"message"` de respostas JSON (ex: HTTP 4xx/5xx). Opcionalmente registar `state.getError()` para diagnóstico. |

---

## Como adicionar um novo endpoint (referência futura)

### 1. Criar a interface Retrofit em `api/services/`

```java
// src/main/java/com/gestaoiogurtes/api/services/IFornecedorApiService.java

public interface IFornecedorApiService {

    @GET("api/fornecedores")
    Call<List<FornecedorResponse>> findAll();

    @POST("api/fornecedores")
    Call<FornecedorResponse> create(@Body CreateFornecedorRequest request);
}
```

### 2. Obter a instância via RetrofitClient

```java
IFornecedorApiService api = RetrofitClient.getInstance()
                                          .getService(IFornecedorApiService.class);
```

> `RetrofitClient.getService()` cria o proxy Retrofit internamente e reutiliza
> o mesmo `OkHttpClient` partilhado. Não precisas de instanciar nada manualmente.

### 3. Chamar via ApiQuery.execute()

```java
ApiQuery.execute(api.findAll(), state -> {
    if (state.isSuccess()) {
        fornecedores.setAll(state.getData());
    }
});
```

---

## Configuração

### Como mudar a BASE_URL

Edita **apenas** o ficheiro `src/main/resources/config.properties`:

```properties
# Ambiente de desenvolvimento local
api.base.url=http://localhost:8080/

# Exemplo: servidor de produção
# api.base.url=https://api.gestaoiogurtes.pt/
```

> A URL **deve** terminar com `/` (barra). O Retrofit concatena os paths
> dos endpoints directamente a esta base.

### Como mudar o timeout

```properties
# Tempo máximo de espera por resposta (em segundos)
api.timeout.seconds=30
```

O valor aplica-se igualmente ao `connectTimeout`, `readTimeout` e `writeTimeout`
do OkHttp.

### Como activar/desactivar o logging HTTP

```properties
# true  → imprime os headers e corpos de todos os pedidos/respostas na consola
# false → sem logging (recomendado para produção)
api.logging.enabled=true
```

Quando activo, o `HttpLoggingInterceptor` do OkHttp escreve para `System.out`
com nível `BODY` (inclui URL, headers e corpo JSON completo).

---

## Estrutura de ficheiros relevantes

```
src/
├── main/
│   ├── java/com/gestaoiogurtes/
│   │   ├── config/
│   │   │   └── ApiConfig.java                ← lê config.properties para constantes estáticas
│   │   ├── api/
│   │   │   ├── RetrofitClient.java           ← singleton Retrofit + OkHttp
│   │   │   ├── QueryState.java               ← estado imutável do pedido HTTP
│   │   │   ├── ApiQuery.java                 ← executor assíncrono + Platform.runLater()
│   │   │   └── services/
│   │   │       └── IEmpresaApiService.java   ← interface Retrofit (anotações @GET, @POST, …)
│   │   ├── services/
│   │   │   └── EmpresaService.java           ← serviço da aplicação (chamadas HTTP assíncronas via ApiQuery)
│   │   └── utils/
│   │       └── MessageHelper.java            ← mensagens de feedback (AtlantaFX Message)
│   └── resources/
│       └── config.properties                 ← URL base, timeout, logging
docs/
└── API-CLIENT.md                             ← este ficheiro
```



# WEB

✅ API Client implementado na Web App
Ficheiros criados
Ficheiro	Descrição


pom.xml
+4 dependências: Retrofit 2.11.0, converter-gson, OkHttp 4.12.0, logging-interceptor


config.properties
api.base.url, api.timeout.seconds, api.logging.enabled — idêntico à desktop app


ApiConfig.java
Lê config.properties para constantes estáticas — cópia exacta


QueryState.java
State machine IDLE→LOADING→SUCCESS/ERROR — cópia exacta


RetrofitClient.java
Singleton OkHttp + Retrofit + adaptadores Gson LocalDate/LocalDateTime — cópia exacta


ApiQuery.java
Executor — adaptado: usa call.execute() síncrono em vez de call.enqueue() + Platform.runLater() (que não existe em Spring Boot)
api/services/	Directório criado e pronto para receber as interfaces Retrofit
Como usar (idêntico à desktop app)
java
// 1. Criar interface Retrofit em api/services/
// 2. Obter instância
IMinhaApiService api = RetrofitClient.getInstance().getService(IMinhaApiService.class);
// 3. Executar via ApiQuery
ApiQuery.execute(api.findAll(), state -> {
    if (state.isLoading())  { /* pedido iniciado */ }
    if (state.isSuccess())  { var dados = state.getData(); }
    if (state.isError())    { var msg = state.getErrorMessage(); }
});