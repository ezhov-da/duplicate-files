package ru.ezhov.duplicate.files;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateMd5DuplicateFilesXml {
    private static final Logger LOG = Logger.getLogger(CreateMd5DuplicateFilesXml.class.getName());

    public void create(File root, File xmlReport) throws Exception {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(xmlReport), StandardCharsets.UTF_8)) {
            LOG.log(Level.INFO, "начало записи xml файла");
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement("files");
            fillMd5FilesRecursive(root, md5File -> {
                try {
                    xmlStreamWriter.writeStartElement("file");
                    xmlStreamWriter.writeAttribute("md5", md5File.getMd5Stamp());
                    xmlStreamWriter.writeCharacters(md5File.getFile().getAbsolutePath());
                    xmlStreamWriter.writeEndElement();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            });
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
            LOG.log(Level.INFO, "xml файл записан ''{0}''", xmlReport.getAbsolutePath());
        }
    }

    private void fillMd5FilesRecursive(File file, MD5FileListener md5FileListener) throws Exception {
        if (file.isDirectory()) {
            LOG.log(Level.INFO, "обработка папки ''{0}''", file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    fillMd5FilesRecursive(f, md5FileListener);
                }
            }
        } else {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(file.getPath())));
            byte[] digest = md.digest();
            String myChecksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
            md5FileListener.action(new Md5File(myChecksum, file));
        }
    }

    private interface MD5FileListener {
        void action(Md5File md5File);
    }

    private class Md5File {
        private String md5Stamp;
        private File file;

        Md5File(String md5Stamp, File file) {
            this.md5Stamp = md5Stamp;
            this.file = file;
        }

        public String getMd5Stamp() {
            return md5Stamp;
        }

        public File getFile() {
            return file;
        }
    }
}
