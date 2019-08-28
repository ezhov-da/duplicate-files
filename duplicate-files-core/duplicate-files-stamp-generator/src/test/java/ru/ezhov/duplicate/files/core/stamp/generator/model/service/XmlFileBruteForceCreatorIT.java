package ru.ezhov.duplicate.files.core.stamp.generator.model.service;

import org.junit.Test;

import java.io.File;

public class XmlFileBruteForceCreatorIT {

    @Test
    public void run() {
        XmlFileBruteForceCreator xmlFileBruteForceCreator =
                new XmlFileBruteForceCreator(new File("D:/изображения/жена-mi-20190317"), new File("D:/duplicate-files-md5.xml"));
        xmlFileBruteForceCreator.run(System.out::println);
    }
}