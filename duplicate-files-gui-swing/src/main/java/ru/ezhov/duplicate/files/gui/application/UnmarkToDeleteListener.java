package ru.ezhov.duplicate.files.gui.application;


import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;

public interface UnmarkToDeleteListener {
    void unmark(FilePath filePath);
}
