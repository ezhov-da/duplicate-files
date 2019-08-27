package ru.ezhov.duplicate.files.gui.analyse.domain;

import ru.ezhov.duplicate.files.core.stamp.analyzer.domain.DuplicateId;

public class Md5Hash {
    String id;

    public Md5Hash(DuplicateId duplicateId) {
        this.id = duplicateId.id();
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}