package ru.ezhov.duplicate.files.gui.application.delete.domain;

import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;

import java.io.File;
import java.util.Objects;

public class PreparedToDelete {
    private FilePath filePath;

    private File file;

    public PreparedToDelete(FilePath filePath) {
        this.filePath = filePath;
        this.file = new File(filePath.path());
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return filePath.path();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreparedToDelete that = (PreparedToDelete) o;
        return Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath);
    }
}
