package ru.ezhov.duplicate.files.gui.application.repository;

import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;

import java.awt.image.BufferedImage;

public interface ThumbnailsRepository {
    BufferedImage by(FilePath filePath);
}
