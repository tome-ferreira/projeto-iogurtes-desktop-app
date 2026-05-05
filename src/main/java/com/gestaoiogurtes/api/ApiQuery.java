package com.gestaoiogurtes.api;

import javafx.application.Platform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.function.Consumer;

/**
 * Utilitário de execução assíncrona de pedidos Retrofit integrado com o
 * ciclo de vida do JavaFX.
 *
 * <h3>Responsabilidades</h3>
 * <ol>
 *   <li>Emite {@link QueryState#loading()} <em>imediatamente</em> antes de
 *       enfileirar o pedido HTTP.</li>
 *   <li>Delega a execução HTTP à thread pool interna do OkHttp via
 *       {@link Call#enqueue(Callback)}.</li>
 *   <li>Devolve o resultado ao controlador JavaFX através de
 *       {@code Platform.runLater()} — garantindo que as actualizações ao
 *       estado da UI acontecem sempre na JavaFX Application Thread.</li>
 * </ol>
 *
 * <p><strong>Regra importante:</strong> {@code Platform.runLater()} só é
 * chamado aqui. Os controllers <em>nunca</em> o invocam directamente.</p>
 *
 * <h3>Exemplo de utilização num controller JavaFX</h3>
 * <pre>{@code
 * // 1. Obter a interface Retrofit
 * IIogurtesApi api = RetrofitClient.getInstance().getService(IIogurtesApi.class);
 *
 * // 2. Criar o Call
 * Call<List<IogurteVM>> call = api.listarTodos();
 *
 * // 3. Executar via ApiQuery
 * ApiQuery.execute(call, state -> {
 *     if (state.isLoading()) {
 *         progressBar.setVisible(true);
 *     } else if (state.isSuccess()) {
 *         progressBar.setVisible(false);
 *         tabela.getItems().setAll(state.getData());
 *     } else if (state.isError()) {
 *         progressBar.setVisible(false);
 *         mostrarErro(state.getErrorMessage());
 *     }
 * });
 * }</pre>
 */
public final class ApiQuery {

    /** Classe utilitária — não instanciável. */
    private ApiQuery() {}

    /**
     * Executa um pedido Retrofit de forma assíncrona e notifica o chamador
     * através do {@code onStateChange} com transições de estado.
     *
     * <p>Sequência de chamadas garantidas:</p>
     * <ol>
     *   <li>{@code onStateChange(QueryState.loading())} — imediatamente, na
     *       thread do chamador.</li>
     *   <li>{@code onStateChange(QueryState.success(body))} <em>ou</em>
     *       {@code onStateChange(QueryState.error(...))} — posteriormente, na
     *       JavaFX Application Thread via {@code Platform.runLater()}.</li>
     * </ol>
     *
     * @param <T>           tipo do corpo da resposta esperada pelo Retrofit
     * @param call          pedido Retrofit ainda não executado
     * @param onStateChange callback invocado em cada transição de estado;
     *                      as chamadas pós-{@code loading()} ocorrem na JavaFX
     *                      Application Thread
     */
    public static <T> void execute(Call<T> call, Consumer<QueryState<T>> onStateChange) {
        // ── 1. Notificar imediatamente que o pedido começou ──────────────────
        onStateChange.accept(QueryState.loading());

        // ── 2. Enfileirar na thread pool do OkHttp ───────────────────────────
        call.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    // ── 3a. Sucesso: devolver na JavaFX Application Thread ────
                    T body = response.body();
                    Platform.runLater(() ->
                            onStateChange.accept(QueryState.success(body)));
                } else {
                    // Resposta HTTP mas com código de erro (4xx / 5xx)
                    String message = "Erro HTTP " + response.code() +
                                     (response.message() != null
                                             ? " – " + response.message()
                                             : "");
                    Platform.runLater(() ->
                            onStateChange.accept(QueryState.error(message, null)));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                // ── 3b. Falha de rede: devolver na JavaFX Application Thread ─
                String message = t.getMessage() != null
                        ? t.getMessage()
                        : "Falha de rede desconhecida";
                Platform.runLater(() ->
                        onStateChange.accept(QueryState.error(message, t)));
            }
        });
    }
}
