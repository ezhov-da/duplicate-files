package ru.ezhov.duplicate.files.stamp.analyzer.model.service;

import java.util.List;
import java.util.Map;

public interface DuplicateFingerPrintFilesAnalyserService {
    Map<DuplicateId, List<FilePath>> findDuplicate(List<FingerprintFile> fingerprintFiles);
}
