package dev.overtow.service;

public interface Config {
    String getString(String key);

    int getInteger(String key);
}
