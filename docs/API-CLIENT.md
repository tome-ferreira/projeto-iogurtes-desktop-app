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
// ─── Num controller JavaFX (ex.: IogurtesController) ───────────────────────

@FXML private TableView<IogurteVM> tabela;
@FXML private ProgressIndicator    spinner;
@FXML private Label                lblErro;

private void carregarIogurtes() {
    // 1. Obter a interface Retrofit
    IIogurtesRetrofitApi api = RetrofitClient.getInstance()
                                             .getService(IIogurtesRetrofitApi.class);

    // 2. Criar o Call (pedido ainda não enviado)
    Call<List<IogurteVM>> call = api.listarTodos();

    // 3. Executar via ApiQuery — tratar cada estado no mesmo lambda
    ApiQuery.execute(call, state -> {
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

import com.gestaoiogurtes.model.IogurteVM;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface IIogurtesRetrofitApi {

    @GET("api/iogurtes")
    Call<List<IogurteVM>> listarTodos();

    @POST("api/iogurtes")
    Call<IogurteVM> adicionar(@Body IogurteVM iogurte);

    @PUT("api/iogurtes/{id}")
    Call<IogurteVM> atualizar(@Path("id") String id, @Body IogurteVM iogurte);

    @DELETE("api/iogurtes/{id}")
    Call<Void> remover(@Path("id") String id);
}
```

### Passo 2 — Obter a instância via RetrofitClient

```java
IIogurtesRetrofitApi api = RetrofitClient.getInstance()
                                         .getService(IIogurtesRetrofitApi.class);
```

### Passo 3 — Criar um Call e passar ao ApiQuery

```java
Call<List<IogurteVM>> call = api.listarTodos();

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
// src/main/java/com/gestaoiogurtes/api/services/IEncomendaRetrofitApi.java

public interface IEncomendaRetrofitApi {

    @GET("api/encomendas")
    Call<List<EncomendaVM>> listarTodas();

    @POST("api/encomendas")
    Call<EncomendaVM> criar(@Body EncomendaVM encomenda);
}
```

### 2. Obter a instância via RetrofitClient

```java
IEncomendaRetrofitApi api = RetrofitClient.getInstance()
                                          .getService(IEncomendaRetrofitApi.class);
```

> `RetrofitClient.getService()` cria o proxy Retrofit internamente e reutiliza
> o mesmo `OkHttpClient` partilhado. Não precisas de instanciar nada manualmente.

### 3. Chamar via ApiQuery.execute()

```java
ApiQuery.execute(api.listarTodas(), state -> {
    if (state.isSuccess()) {
        encomendas.setAll(state.getData());
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

## Estrutura de ficheiros criados

```
src/
├── main/
│   ├── java/com/gestaoiogurtes/
│   │   ├── config/
│   │   │   └── ApiConfig.java          ← lê config.properties para constantes estáticas
│   │   └── api/
│   │       ├── RetrofitClient.java     ← singleton Retrofit + OkHttp
│   │       ├── QueryState.java         ← estado imutável do pedido HTTP
│   │       └── ApiQuery.java           ← executor assíncrono + Platform.runLater()
│   └── resources/
│       └── config.properties           ← URL base, timeout, logging
docs/
└── API-CLIENT.md                       ← este ficheiro
```
