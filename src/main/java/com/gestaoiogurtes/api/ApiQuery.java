package com.gestaoiogurtes.api;

import javafx.application.Platform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.function.Consumer;

public final class ApiQuery {

    private ApiQuery() {
    }

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
                    Platform.runLater(() -> onStateChange.accept(QueryState.success(body)));
                } else {
                    // Tenta extrair "message" do errorBody JSON
                    String message = "Erro HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            com.google.gson.JsonObject json = com.google.gson.JsonParser
                                    .parseString(errorBody).getAsJsonObject();
                            if (json.has("message")) {
                                message = json.get("message").getAsString();
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    final String finalMessage = message;
                    Platform.runLater(() -> onStateChange.accept(QueryState.error(finalMessage, null)));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                // ── 3b. Falha de rede: devolver na JavaFX Application Thread ─
                String message = t.getMessage() != null
                        ? t.getMessage()
                        : "Falha de rede desconhecida";
                Platform.runLater(() -> onStateChange.accept(QueryState.error(message, t)));
            }
        });
    }
}
