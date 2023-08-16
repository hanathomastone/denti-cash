package com.kaii.dentix.global.common.dto;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;

@AllArgsConstructor
public class PagingRequest {

    private int page;

    private int size;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public PageRequest of() {
        return PageRequest.of(page - 1, size);
    }

}
