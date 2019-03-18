package ru.ezhov.duplicate.files;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
        List<Md5File> md5FileList = fillMd5FilesRecursive(root, new ArrayList<>());
        try (OutputStream outputStream = new FileOutputStream(xmlReport)) {
            LOG.log(Level.INFO, "начало записи xml файла");
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement("files");
            for (Md5File md5File : md5FileList) {
                xmlStreamWriter.writeStartElement("file");
                xmlStreamWriter.writeAttribute("md5", md5File.getMd5Stamp());
                xmlStreamWriter.writeCharacters(md5File.getFile().getAbsolutePath());
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            LOG.log(Level.INFO, "xml файл записан ''{0}''", xmlReport.getAbsolutePath());
        }
    }

    private List<Md5File> fillMd5FilesRecursive(File file, List<Md5File> md5FileList) throws Exception {
        if (file.isDirectory()) {
            LOG.log(Level.INFO, "обработка папки ''{0}''", file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    fillMd5FilesRecursive(f, md5FileList);
                }
            }
        } else {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(file.getPath())));
            byte[] digest = md.digest();
            String myChecksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
            md5FileList.add(new Md5File(myChecksum, file));
        }
        return md5FileList;
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
