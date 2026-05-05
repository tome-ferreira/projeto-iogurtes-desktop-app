package com.gestaoiogurtes.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lê as configurações do cliente HTTP a partir de {@code config.properties}
 * (em {@code src/main/resources/config.properties}).
 *
 * <p>Os valores são carregados uma única vez na inicialização da classe e
 * expostos como constantes estáticas imutáveis.</p>
 *
 * <h3>Propriedades suportadas</h3>
 * <ul>
 *   <li>{@code api.base.url}         – URL base da API REST (com trailing slash)</li>
 *   <li>{@code api.timeout.seconds}  – Timeout HTTP em segundos</li>
 *   <li>{@code api.logging.enabled}  – Activar logging detalhado de pedidos/respostas</li>
 * </ul>
 */
public final class ApiConfig {

    /** URL base da API REST, p. ex. {@code http://localhost:8080/}. */
    public static final String BASE_URL;

    /** Timeout de conexão, leitura e escrita em segundos. */
    public static final int TIMEOUT;

    /** Se {@code true}, o {@code HttpLoggingInterceptor} regista o corpo completo dos pedidos HTTP. */
    public static final boolean LOGGING_ENABLED;

    // ──────────────────────────────────────────────────────────────────────────
    // Bloco estático: carregado apenas uma vez, à primeira referência à classe
    // ──────────────────────────────────────────────────────────────────────────
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

        BASE_URL        = props.getProperty("api.base.url", "http://localhost:8080/");
        TIMEOUT         = Integer.parseInt(props.getProperty("api.timeout.seconds", "30"));
        LOGGING_ENABLED = Boolean.parseBoolean(props.getProperty("api.logging.enabled", "false"));
    }

    /** Classe utilitária – não instanciável. */
    private ApiConfig() {}
}
