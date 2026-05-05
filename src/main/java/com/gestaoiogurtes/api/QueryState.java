package com.gestaoiogurtes.api;

/**
 * Representa o estado de uma operação assíncrona iniciada por {@link ApiQuery}.
 *
 * <p>Inspirado no padrão <em>ReactQuery</em>, encapsula o ciclo de vida
 * completo de um pedido HTTP em quatro estados mutuamente exclusivos:</p>
 *
 * <pre>
 *  IDLE → LOADING → SUCCESS
 *                 ↘ ERROR
 * </pre>
 *
 * <h3>Criação</h3>
 * <p>Usa os métodos de fábrica estáticos — nunca o construtor directamente:</p>
 * <pre>{@code
 * QueryState<List<IogurteVM>> estado = QueryState.loading();
 * QueryState<List<IogurteVM>> ok     = QueryState.success(lista);
 * QueryState<List<IogurteVM>> falhou = QueryState.error("Sem rede", ex);
 * }</pre>
 *
 * @param <T> tipo do corpo da resposta esperada
 */
public class QueryState<T> {

    // ──────────────────────────────────────────────────────────────────────────
    // Enum de estados
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Ciclo de vida de um pedido assíncrono.
     * <ul>
     *   <li>{@link #IDLE}    – nenhum pedido foi iniciado ainda</li>
     *   <li>{@link #LOADING} – pedido em curso no background</li>
     *   <li>{@link #SUCCESS} – resposta recebida com sucesso; {@code data} está preenchido</li>
     *   <li>{@link #ERROR}   – pedido falhou; {@code errorMessage} e {@code error} estão preenchidos</li>
     * </ul>
     */
    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Campos
    // ──────────────────────────────────────────────────────────────────────────

    private final Status    status;
    private final T         data;
    private final String    errorMessage;
    private final Throwable error;

    // ──────────────────────────────────────────────────────────────────────────
    // Construtor privado — usar os métodos de fábrica
    // ──────────────────────────────────────────────────────────────────────────

    private QueryState(Status status, T data, String errorMessage, Throwable error) {
        this.status       = status;
        this.data         = data;
        this.errorMessage = errorMessage;
        this.error        = error;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Métodos de fábrica estáticos
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Cria um {@code QueryState} em estado inicial, sem pedido activo.
     *
     * @param <T> tipo do dado esperado
     * @return estado {@link Status#IDLE}
     */
    public static <T> QueryState<T> idle() {
        return new QueryState<>(Status.IDLE, null, null, null);
    }

    /**
     * Cria um {@code QueryState} indicando que o pedido está em curso.
     *
     * @param <T> tipo do dado esperado
     * @return estado {@link Status#LOADING}
     */
    public static <T> QueryState<T> loading() {
        return new QueryState<>(Status.LOADING, null, null, null);
    }

    /**
     * Cria um {@code QueryState} com resposta bem-sucedida.
     *
     * @param <T>  tipo do dado recebido
     * @param data corpo da resposta; pode ser {@code null} se a API devolver 204 No Content
     * @return estado {@link Status#SUCCESS}
     */
    public static <T> QueryState<T> success(T data) {
        return new QueryState<>(Status.SUCCESS, data, null, null);
    }

    /**
     * Cria um {@code QueryState} indicando que o pedido falhou.
     *
     * @param <T>    tipo do dado esperado (nunca chegará a estar preenchido)
     * @param message mensagem de erro legível para o utilizador ou para logging
     * @param cause   excepção original lançada pela rede ou pelo Retrofit
     * @return estado {@link Status#ERROR}
     */
    public static <T> QueryState<T> error(String message, Throwable cause) {
        return new QueryState<>(Status.ERROR, null, message, cause);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Predicados de conveniência
    // ──────────────────────────────────────────────────────────────────────────

    /** @return {@code true} se o pedido ainda estiver em curso */
    public boolean isLoading() {
        return status == Status.LOADING;
    }

    /** @return {@code true} se a resposta foi recebida com sucesso */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /** @return {@code true} se o pedido terminou com erro */
    public boolean isError() {
        return status == Status.ERROR;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Getters
    // ──────────────────────────────────────────────────────────────────────────

    /** @return estado actual do ciclo de vida */
    public Status getStatus() {
        return status;
    }

    /**
     * @return dados recebidos da API; {@code null} se o estado não for {@link Status#SUCCESS}
     */
    public T getData() {
        return data;
    }

    /**
     * @return mensagem de erro; {@code null} se o estado não for {@link Status#ERROR}
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return causa original do erro; {@code null} se o estado não for {@link Status#ERROR}
     */
    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "QueryState{status=" + status +
               (data         != null ? ", data="         + data         : "") +
               (errorMessage != null ? ", errorMessage=" + errorMessage : "") +
               "}";
    }
}
