package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service;

import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFingerPrintFilesAnalyserService;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FingerprintFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultDuplicateFingerPrintFilesAnalyserService implements DuplicateFingerPrintFilesAnalyserService {
    public Map<DuplicateId, List<FilePath>> findDuplicate(List<FingerprintFile> fingerprintFiles) {
        Map<DuplicateId, List<FilePath>> map = new HashMap<>();
        fingerprintFiles.forEach(fpf -> {
                    DuplicateId duplicateId = new DuplicateIdImpl(fpf.fingerprint());
                    FilePath filePath = new FilePathImpl(fpf.file().getAbsolutePath());
                    List<FilePath> paths = map.get(duplicateId);
                    if (paths == null) {
                        paths = new ArrayList<>();
                        paths.add(new FilePathImpl(fpf.file().getAbsolutePath()));
                        map.put(duplicateId, paths);
                    } else {
                        paths.add(filePath);
                    }
                }
        );
        return map;
    }
}
