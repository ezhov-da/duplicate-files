package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service;

import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;

import java.util.Objects;

class FilePathImpl implements FilePath {
    private String path;

    FilePathImpl(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePathImpl filePath = (FilePathImpl) o;
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
