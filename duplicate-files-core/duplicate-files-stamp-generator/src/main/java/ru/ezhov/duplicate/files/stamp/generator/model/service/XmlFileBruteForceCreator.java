package ru.ezhov.duplicate.files.stamp.generator.model.service;

import ru.ezhov.duplicate.files.stamp.generator.infrastructure.generator.Md5StampGenerator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XmlFileBruteForceCreator {
    private static final Logger LOG = Logger.getLogger(XmlFileBruteForceCreator.class.getName());
    private File root;
    private File report;
    private FileBruteForceService fileBruteForceService;

    public XmlFileBruteForceCreator(File root, File report) {
        fileBruteForceService = new FileBruteForceService(root, new Md5StampGenerator());
        this.root = root;
        this.report = report;
    }

    public void run(FileListener fileListener) {
        XMLStreamWriter xmlStreamWriter = null;
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(report), StandardCharsets.UTF_8)) {
            LOG.log(Level.CONFIG, "начало записи xml файла");
            xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("duplicate-files");
            xmlStreamWriter.writeAttribute("type", "stamps");
            xmlStreamWriter.writeAttribute("version", "0.1");
            xmlStreamWriter.writeStartElement("files");
            try {
                XMLStreamWriter finalXmlStreamWriter = xmlStreamWriter;
                fileBruteForceService.bruteForceStart(fileStamp -> {
                    try {
                        if (fileListener != null) {
                            fileListener.stampOn(fileStamp.file().getAbsolutePath());
                        }
                        finalXmlStreamWriter.writeStartElement("file");
                        finalXmlStreamWriter.writeAttribute("stamp", fileStamp.stamp());
                        finalXmlStreamWriter.writeCharacters(fileStamp.file().getAbsolutePath());
                        finalXmlStreamWriter.writeEndElement();
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Throwable e) {
                LOG.log(Level.WARNING, "Ошибка при создании отпечатка файла", e);
            }
            xmlStreamWriter.writeEndElement();//files
            xmlStreamWriter.writeEndElement();//duplicate-files
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
            LOG.log(Level.CONFIG, "xml файл записан ''{0}''", report.getAbsolutePath());
        } catch (XMLStreamException e) {
            LOG.log(Level.SEVERE, "Ошибка записи XML", e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Ошибка при записи файла", e);
        }
    }

    public void stop() {
        try {
            fileBruteForceService.stopBruteForce();
        } catch (FileBruteForceServiceAlreadyStoppedException e) {
            e.printStackTrace();
        }
    }
}
