package com.gestaoiogurtes.models;

import java.util.List;

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
