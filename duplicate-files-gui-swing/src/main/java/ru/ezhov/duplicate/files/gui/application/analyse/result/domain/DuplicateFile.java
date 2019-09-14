package ru.ezhov.duplicate.files.gui.application.analyse.result.domain;

import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;

import java.io.File;

public class DuplicateFile {
    private FilePath filePath;
    private File file;
    private boolean markDeleted;

    public DuplicateFile(FilePath path) {
        this.filePath = path;
        this.file = new File(this.filePath.path());
    }

    public FilePath getPath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }

    public boolean isMarkDeleted() {
        return markDeleted;
    }

    public void markDeleted() {
        this.markDeleted = true;
    }

    public void unarkDeleted() {
        this.markDeleted = false;
    }

    @Override
    public String toString() {
        return filePath.path();
    }
}
