package com.uniquindio.skyline.application.dto.response;

import com.uniquindio.skyline.domain.repository.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int number,
        int size
) {
    // Turns a page of domain objects into a page of responses, converting each element.
    public static <D, T> PageResponse<T> from(PageResult<D> page, Function<D, T> converter) {
        return new PageResponse<>(
                page.content().stream().map(converter).toList(),
                page.totalElements(),
                page.totalPages(),
                page.number(),
                page.size());
    }
}