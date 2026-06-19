package com.networx.networx.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> items;
    private long totalItems;
    private int totalPages;
    private int currPage;
    private int limit;
    private boolean hasNextPage;
}

