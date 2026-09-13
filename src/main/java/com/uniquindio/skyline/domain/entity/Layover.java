package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.Airport;
import lombok.Getter;

import java.time.Duration;
import java.util.Objects;

@Getter
public class Layover {

    String id;
    Airport airport;
    Duration duration;

    public Layover(Airport airport, Duration duration) {
        this.airport = airport;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Layover)) return false;
        Layover other = (Layover) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
