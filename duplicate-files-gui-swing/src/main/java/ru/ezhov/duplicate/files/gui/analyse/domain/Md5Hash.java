package ru.ezhov.duplicate.files.gui.analyse.domain;

public class Md5Hash {
    String hash;

    public Md5Hash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return hash;
    }
}