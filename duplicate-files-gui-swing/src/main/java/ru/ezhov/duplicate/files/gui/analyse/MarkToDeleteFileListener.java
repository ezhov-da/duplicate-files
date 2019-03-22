package ru.ezhov.duplicate.files.gui.analyse;

public interface MarkToDeleteFileListener {
    void mark(String filePath);

    void removeMark(String filePath);
}
