package ru.ezhov.duplicate.files.stamp.analyzer.model.domain;

import java.util.Objects;

public class FilePath {
    private String path;

    public FilePath(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePath filePath = (FilePath) o;
        return Objects.equals(path, filePath.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return path;
    }
}
