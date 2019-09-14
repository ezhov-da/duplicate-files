package ru.ezhov.duplicate.files.stamp.analyzer.model.service;

import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.DuplicateId;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class DuplicateFilesAnalyserService {
    public Map<DuplicateId, List<FilePath>> findDuplicate(File xmlFileReport) throws DuplicateFilesAnalyserServiceException {
        if (xmlFileReport == null) {
            throw new DuplicateFilesAnalyserServiceException("Файл не можеть быть null");
        }
        if (!xmlFileReport.exists()) {
            throw new DuplicateFilesAnalyserServiceException("Файл '" + xmlFileReport.getAbsolutePath() + "' должен существовать");
        }
        Map<DuplicateId, List<FilePath>> map = new HashMap<>();
        try {
            try (InputStream inputStream = new FileInputStream(xmlFileReport)) {
                XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
                DuplicateId duplicateId = null;
                String lastPath = null;
                while (xmlEventReader.hasNext()) {
                    XMLEvent xmlEvent = xmlEventReader.nextEvent();
                    if (xmlEvent.isStartElement()) {
                        StartElement startElement = xmlEvent.asStartElement();
                        String name = startElement.getName().toString();
                        if ("file".equals(name)) {
                            Iterator iterator = startElement.getAttributes();
                            while (iterator.hasNext()) {
                                Attribute o = (Attribute) iterator.next();
                                duplicateId = new DuplicateId(o.getValue());
                            }

                        }
                    } else if (xmlEvent.isCharacters()) {
                        Characters characters = xmlEvent.asCharacters();
                        String lastPathRaw = characters.getData().trim();
                        if ("".equals(lastPathRaw)) {
                            lastPath = null;
                        } else {
                            lastPath = lastPathRaw;
                        }
                    }
                    if (duplicateId != null && lastPath != null) {
                        List<FilePath> paths = map.get(duplicateId);
                        if (paths == null) {
                            paths = new ArrayList<>();
                            paths.add(new FilePath(lastPath));
                            map.put(duplicateId, paths);
                        } else {
                            paths.add(new FilePath(lastPath));
                        }
                        duplicateId = null;
                        lastPath = null;
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new DuplicateFilesAnalyserServiceException("Ошибка при анализе дубликатов в файле '" + xmlFileReport.getAbsolutePath() + "'", e);
        }
    }
}
