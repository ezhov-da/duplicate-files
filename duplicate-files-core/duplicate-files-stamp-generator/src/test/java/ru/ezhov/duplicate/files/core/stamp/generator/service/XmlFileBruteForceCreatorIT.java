package ru.ezhov.duplicate.files.core.stamp.generator.service;

import org.junit.Test;

import java.io.File;

public class XmlFileBruteForceCreatorIT {

    @Test
    public void run() {
        XmlFileBruteForceCreator xmlFileBruteForceCreator =
                new XmlFileBruteForceCreator(new File("D:/изображения/жена-mi-20190317"), new File("D:/duplicate-files-md5.xml"));
        xmlFileBruteForceCreator.run(new FileListener() {
            @Override
            public void stampOn(String absoluteFilePath) {
                System.out.println(absoluteFilePath);
            }
        });
    }
}