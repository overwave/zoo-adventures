package dev.overtow.service.config;

public interface Config {
    String getString(String key);

    int getInteger(String key);
}
