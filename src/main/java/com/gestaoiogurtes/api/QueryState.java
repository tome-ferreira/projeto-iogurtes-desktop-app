package com.gestaoiogurtes.api;

public class QueryState<T> {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    // Campos
    private final Status    status;
    private final T         data;
    private final String    errorMessage;
    private final Throwable error;


    private QueryState(Status status, T data, String errorMessage, Throwable error) {
        this.status       = status;
        this.data         = data;
        this.errorMessage = errorMessage;
        this.error        = error;
    }


    public static <T> QueryState<T> idle() {
        return new QueryState<>(Status.IDLE, null, null, null);
    }

    public static <T> QueryState<T> loading() {
        return new QueryState<>(Status.LOADING, null, null, null);
    }

    public static <T> QueryState<T> success(T data) {
        return new QueryState<>(Status.SUCCESS, data, null, null);
    }

    public static <T> QueryState<T> error(String message, Throwable cause) {
        return new QueryState<>(Status.ERROR, null, message, cause);
    }


    //retorna true se o pedido ainda estiver em curso 
    public boolean isLoading() {
        return status == Status.LOADING;
    }

    //retorna true se a resposta foi recebida com sucesso 
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    //retorna true se o pedido terminou com erro 
    public boolean isError() {
        return status == Status.ERROR;
    }

    
    // Getters
    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

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
