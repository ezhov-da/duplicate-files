package ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.generator;

import ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.service.StampGenerator;
import ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.service.StampGeneratorException;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class Md5StampGenerator implements StampGenerator {
    @Override
    public String generate(File file) throws StampGeneratorException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(file.getPath())));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (Exception e) {
            throw new StampGeneratorException(e);
        }
    }
}
