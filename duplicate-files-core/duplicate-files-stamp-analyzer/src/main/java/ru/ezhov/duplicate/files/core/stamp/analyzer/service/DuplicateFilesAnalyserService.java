package ru.ezhov.duplicate.files.core.stamp.analyzer.service;

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
    public Map<String, List<String>> findDuplicate(File xmlFileReport) throws DuplicateFilesAnalyserServiceException {
        Map<String, List<String>> map = new HashMap<>();
        try {

            try (InputStream inputStream = new FileInputStream(xmlFileReport)) {
                XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
                String lastMd5 = null;
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
                                lastMd5 = o.getValue();
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
                    if (lastMd5 != null && lastPath != null) {
                        List<String> paths = map.get(lastMd5);
                        if (paths == null) {
                            paths = new ArrayList<>();
                            paths.add(lastPath);
                            map.put(lastMd5, paths);
                        } else {
                            paths.add(lastPath);
                        }
                        lastMd5 = null;
                        lastPath = null;
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new DuplicateFilesAnalyserServiceException(e);
        }
    }
}
