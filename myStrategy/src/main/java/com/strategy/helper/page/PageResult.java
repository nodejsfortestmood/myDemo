package com.strategy.helper.page;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private List<T> data;
    private long total;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
