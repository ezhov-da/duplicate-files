package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service;

import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFingerPrintFilesAnalyserService;

public abstract class DuplicateFingerPrintFilesAnalyserServiceFactory {
    public static DuplicateFingerPrintFilesAnalyserService newInstance() {
        return new DefaultDuplicateFingerPrintFilesAnalyserService();
    }
}
