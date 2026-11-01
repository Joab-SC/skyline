package com.uniquindio.skyline.domain.repository;

import java.util.List;

// One page of a search. It is plain Java so the domain does not depend on Spring's Page.
public record PageResult<T>(List<T> content, long totalElements, int totalPages, int number, int size) {}