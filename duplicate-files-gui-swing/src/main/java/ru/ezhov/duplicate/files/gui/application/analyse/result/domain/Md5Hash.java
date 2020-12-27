package ru.ezhov.duplicate.files.gui.application.analyse.result.domain;

import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateId;

public class Md5Hash {
    String id;

    public Md5Hash(DuplicateId DuplicateId) {
        this.id = DuplicateId.id();
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}