package ru.ezhov.duplicate.files.stamp.generator.infrastructure.repository;

import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStamp;
import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStampDefault;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepository;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepositoryException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class XmlFingerprintFileRepository implements FingerprintFileRepository {
    private static final Logger LOG = Logger.getLogger(XmlFingerprintFileRepository.class.getName());
    private File store;

    XmlFingerprintFileRepository(File store) {
        this.store = store;
    }

    @Override
    public void save(List<FileStamp> fileStampDefaults) throws FingerprintFileRepositoryException {
        XMLStreamWriter xmlStreamWriter = null;
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(store), StandardCharsets.UTF_8)) {
            LOG.log(Level.CONFIG, "начало записи xml файла");
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("duplicate-files");
            xmlStreamWriter.writeAttribute("type", "stamps");
            xmlStreamWriter.writeAttribute("version", "0.1");
            xmlStreamWriter.writeStartElement("files");
            try {
                XMLStreamWriter finalXmlStreamWriter = xmlStreamWriter;
                fileStampDefaults.forEach(fileStampDefault -> {
                    try {
                        finalXmlStreamWriter.writeStartElement("file");
                        finalXmlStreamWriter.writeAttribute("stampedOn", fileStampDefault.stamp());
                        finalXmlStreamWriter.writeCharacters(fileStampDefault.file().getAbsolutePath());
                        finalXmlStreamWriter.writeEndElement();
                    } catch (XMLStreamException e) {
                        LOG.log(Level.WARNING, "Ошибка при записи отпечатка '" + fileStampDefault + "' в файл '" + store + "'", e);
                    }
                });
            } catch (Throwable e) {
                LOG.log(Level.WARNING, "Ошибка при создании сохранении отпечатков в файл " + store, e);
            }
            xmlStreamWriter.writeEndElement();//files
            xmlStreamWriter.writeEndElement();//duplicate-files
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
            LOG.log(Level.CONFIG, "method=save action=\"xml файл записан ''{0}''\"", store.getAbsolutePath());
        } catch (XMLStreamException e) {
            throw new FingerprintFileRepositoryException("Ошибка записи XML в файл " + store, e);
        } catch (IOException e) {
            throw new FingerprintFileRepositoryException("Ошибка при записи файла " + store, e);
        }

    }

    @Override
    public List<FileStamp> all() throws FingerprintFileRepositoryException {
        if (store == null) {
            throw new FingerprintFileRepositoryException("Файл не можеть быть null");
        }
        if (!store.exists()) {
            throw new FingerprintFileRepositoryException("Файл '" + store.getAbsolutePath() + "' должен существовать");
        }
        List<FileStamp> fileStamps = new ArrayList<>();
        try {
            try (InputStream inputStream = new FileInputStream(store)) {
                XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
                String lastPath = null;
                String stamp = null;
                while (xmlEventReader.hasNext()) {
                    XMLEvent xmlEvent = xmlEventReader.nextEvent();
                    if (xmlEvent.isStartElement()) {
                        StartElement startElement = xmlEvent.asStartElement();
                        String name = startElement.getName().toString();
                        if ("file".equals(name)) {
                            Iterator iterator = startElement.getAttributes();
                            while (iterator.hasNext()) {
                                Attribute o = (Attribute) iterator.next();
                                stamp = o.getValue();
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
                    if (stamp != null && lastPath != null) {
                        fileStamps.add(new FileStampDefault(stamp, new File(lastPath)));
                        stamp = null;
                        lastPath = null;
                    }
                }
            }
            return fileStamps;
        } catch (Exception e) {
            throw new FingerprintFileRepositoryException("Ошибка при загрузке отпечатков из файла '" + store + "'", e);
        }
    }
}
