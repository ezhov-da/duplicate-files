package ru.ezhov.duplicate.files.gui.application;


import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;

public interface UnmarkToDeleteListener {
    void unmark(FilePath filePath);
}
