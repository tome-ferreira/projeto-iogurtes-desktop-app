package com.gestaoiogurtes.models;

import java.util.List;

/**
 * Wrapper genérico que mapeia a resposta paginada da API.
 * As chaves JSON coincidem exactamente com estes campos.
 */
public class PaginatedResponse<T> {
    public List<T> content;
    public int totalElements;
    public int totalPages;
    public int currentPage;
    public int pageSize;
    public boolean first;
    public boolean last;

    public PaginatedResponse() {
    }
}
