package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service;

import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateId;

import java.util.Objects;

class DuplicateIdImpl implements DuplicateId {
    private final String value;

    DuplicateIdImpl(String value) {
        this.value = value;
    }

    public String id() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuplicateIdImpl duplicateIdImpl = (DuplicateIdImpl) o;
        return Objects.equals(value, duplicateIdImpl.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
