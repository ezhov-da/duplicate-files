package ru.ezhov.duplicate.files.stamp.analyzer.model.domain;

import java.util.Objects;

public class DuplicateId {
    private String value;

    public DuplicateId(String value) {
        this.value = value;
    }

    public String id() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuplicateId duplicateId = (DuplicateId) o;
        return Objects.equals(value, duplicateId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
