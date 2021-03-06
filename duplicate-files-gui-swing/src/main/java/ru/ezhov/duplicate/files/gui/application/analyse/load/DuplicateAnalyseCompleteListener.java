package ru.ezhov.duplicate.files.gui.application.analyse.load;

import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;

import java.util.List;
import java.util.Map;

public interface DuplicateAnalyseCompleteListener {
    public void complete(Map<DuplicateId, List<FilePath>> result);
}
