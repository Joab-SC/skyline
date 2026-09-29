package com.uniquindio.skyline.domain.entity;

import java.util.Objects;

public class ExtraService {
    private String id;
    private String nameService;
    private String description;
    private double nationalPrice;
    private double internationalPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtraService)) return false;
        ExtraService other = (ExtraService) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
