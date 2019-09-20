package ru.ezhov.duplicate.files.stamp.generator.infrastructure.generator;

import org.junit.Test;
import ru.ezhov.duplicate.files.stamp.generator.model.service.StampGeneratorException;

import java.io.File;

public class Md5StampGeneratorTest {

    @Test
    public void generateWhenFileBigger2GbAndOk() throws StampGeneratorException {
        Md5StampGenerator stampGenerator = new Md5StampGenerator();

        String stamp = stampGenerator.generate(new File("../../test-images/aerial-shot-architecture-bridge.jpg"));

        System.out.println(stamp);
    }
}