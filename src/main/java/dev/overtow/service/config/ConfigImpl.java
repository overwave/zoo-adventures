package dev.overtow.service.config;

import dev.overtow.util.injection.Bind;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

@Bind
public class ConfigImpl implements Config {
    private final Properties prop;

    public ConfigImpl() {
        prop = new Properties();
        String fileName = "config.properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
            }
            prop.load(inputStream);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    @Override
    public String getString(String key) {
        return prop.getProperty(key);
    }

    @Override
    public int getInteger(String key) {
        return Integer.parseInt(getString(key));
    }
}
