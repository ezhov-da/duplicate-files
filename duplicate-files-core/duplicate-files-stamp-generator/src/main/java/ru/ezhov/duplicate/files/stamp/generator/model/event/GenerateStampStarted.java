package ru.ezhov.duplicate.files.stamp.generator.model.event;

import java.io.File;

public class GenerateStampStarted extends StampGeneratorDomainEvent {
    private File root;

    public GenerateStampStarted(File root) {
        this.root = root;
    }

    public File root() {
        return root;
    }
}
