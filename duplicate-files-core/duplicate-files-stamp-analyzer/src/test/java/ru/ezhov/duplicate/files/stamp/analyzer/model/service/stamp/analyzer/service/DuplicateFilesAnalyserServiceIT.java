package ru.ezhov.duplicate.files.stamp.analyzer.model.service.stamp.analyzer.service;

import org.junit.Test;
import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFilesAnalyserService;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFilesAnalyserServiceException;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DuplicateFilesAnalyserServiceIT {
    @Test
    public void findDuplicateWithoutError() throws DuplicateFilesAnalyserServiceException {
        DuplicateFilesAnalyserService duplicateFilesAnalyserService = new DuplicateFilesAnalyserService();
        Map<DuplicateId, List<FilePath>> duplicate = duplicateFilesAnalyserService.findDuplicate(new File("D:/duplicate-files-md5.xml"));

        System.out.println(duplicate.size());
    }

}