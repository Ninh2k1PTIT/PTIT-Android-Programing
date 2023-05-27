package com.example.socialnetwork.Model;

import java.util.List;

public class PaginationResponse<T> {
    private List<T> data;
    private Integer currentPage;
    private Integer totalItems;
    private Integer totalPages;

    public PaginationResponse() {
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getTotalPages() {
        return totalPages;
    }
}
