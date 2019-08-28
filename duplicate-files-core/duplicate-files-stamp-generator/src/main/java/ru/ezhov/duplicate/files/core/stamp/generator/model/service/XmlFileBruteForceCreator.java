package ru.ezhov.duplicate.files.core.stamp.generator.model.service;

import ru.ezhov.duplicate.files.core.stamp.generator.infrastructure.generator.Md5StampGenerator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(report), StandardCharsets.UTF_8)) {
            LOG.log(Level.CONFIG, "начало записи xml файла");
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("duplicate-files");
            xmlStreamWriter.writeAttribute("type", "stamps");
            xmlStreamWriter.writeAttribute("version", "0.1");
            xmlStreamWriter.writeStartElement("files");
            try {
                fileBruteForceService.bruteForceStart(fileStamp -> {
                    try {
                        if (fileListener != null) {
                            fileListener.stampOn(fileStamp.file().getAbsolutePath());
                        }
                        xmlStreamWriter.writeStartElement("file");
                        xmlStreamWriter.writeAttribute("stamp", fileStamp.stamp());
                        xmlStreamWriter.writeCharacters(fileStamp.file().getAbsolutePath());
                        xmlStreamWriter.writeEndElement();
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Ошибка при создании отпечатка файла", e);
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
            LOG.log(Level.CONFIG, "xml файл записан ''{0}''", report.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
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
