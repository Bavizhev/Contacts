package com.example.contacts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = loadProperties();

    private static Properties loadProperties() {
        Properties props = new Properties();
        Path path = Paths.get(CONFIG_FILE);

        try (InputStream input = Files.newInputStream(path)) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props;
    }

    public static String getUsername() {
        return properties.getProperty("username");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

}
