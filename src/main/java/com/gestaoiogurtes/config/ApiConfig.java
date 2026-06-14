package com.gestaoiogurtes.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ApiConfig {

    public static final String BASE_URL;

    public static final int TIMEOUT;

    public static final boolean LOGGING_ENABLED;

    static {
        Properties props = new Properties();
        try (InputStream is = ApiConfig.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (is == null) {
                throw new ExceptionInInitializerError(
                        "config.properties não encontrado no classpath. " +
                                "Certifica-te de que existe em src/main/resources/.");
            }
            props.load(is);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Erro ao carregar config.properties: " + e.getMessage());
        }

        BASE_URL = props.getProperty("api.base.url", "http://localhost:8080/");
        TIMEOUT = Integer.parseInt(props.getProperty("api.timeout.seconds", "30"));
        LOGGING_ENABLED = Boolean.parseBoolean(props.getProperty("api.logging.enabled", "false"));
    }

    private ApiConfig() {
    }
}
