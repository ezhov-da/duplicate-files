package ru.ezhov.duplicate.files.gui.analyse.domain;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DuplicateFile {
    private String path;
    private boolean markDeleted;

    private BufferedImage imageIcon;

    public DuplicateFile(String path) {
        this.path = path;
        try {
            BufferedImage originalImage = ImageIO.read(new File(path));
            BufferedImage thumbnail = Thumbnails.of(originalImage)
                    .size(100, 100)
                    .asBufferedImage();
            imageIcon = thumbnail;
        } catch (Exception e) {
            //пофигу, что не смогли уменьшить
        }
    }

    public BufferedImage getImageIcon() {
        return imageIcon;
    }

    public String getPath() {
        return path;
    }

    public boolean isMarkDeleted() {
        return markDeleted;
    }

    public void setMarkDeleted(boolean markDeleted) {
        this.markDeleted = markDeleted;
    }

    @Override
    public String toString() {
        return path;
    }
}