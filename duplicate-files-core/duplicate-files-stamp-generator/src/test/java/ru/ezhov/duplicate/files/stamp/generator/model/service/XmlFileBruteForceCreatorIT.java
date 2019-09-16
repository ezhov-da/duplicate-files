package ru.ezhov.duplicate.files.stamp.generator.model.service;

import org.junit.Test;

import java.io.File;

public class XmlFileBruteForceCreatorIT {

    @Test
    public void run() {
        XmlFileBruteForceCreator xmlFileBruteForceCreator =
                new XmlFileBruteForceCreator(new File("E:\\фото-разобрать"), new File("D:/duplicate-files-md5.xml"));
        xmlFileBruteForceCreator.run(System.out::println);
    }
}