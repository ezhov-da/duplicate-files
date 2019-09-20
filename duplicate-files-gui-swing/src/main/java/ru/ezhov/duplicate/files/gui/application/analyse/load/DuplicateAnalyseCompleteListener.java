package ru.ezhov.duplicate.files.gui.application.analyse.load;

import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;

import java.util.List;
import java.util.Map;

public interface DuplicateAnalyseCompleteListener {
    public void complete(Map<DuplicateId, List<FilePath>> result);
}
