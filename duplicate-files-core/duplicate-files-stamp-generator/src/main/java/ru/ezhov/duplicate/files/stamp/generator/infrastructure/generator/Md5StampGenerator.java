package ru.ezhov.duplicate.files.stamp.generator.infrastructure.generator;

import ru.ezhov.duplicate.files.stamp.generator.model.service.StampGenerator;
import ru.ezhov.duplicate.files.stamp.generator.model.service.StampGeneratorException;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Md5StampGenerator implements StampGenerator {
    @Override
    public String generate(File file) throws StampGeneratorException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                byte[] bytes = new byte[1024];
                int read;
                while ((read = inputStream.read(bytes)) != -1) {
                    md.update(bytes, 0, read);
                }
            }
            byte[] digest = md.digest();
            String s = DatatypeConverter.printHexBinary(digest);
            return s.toLowerCase();
        } catch (Exception e) {
            throw new StampGeneratorException(e);
        }
    }
}
