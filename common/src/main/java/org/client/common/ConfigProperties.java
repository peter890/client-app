/**
 *
 */
package org.client.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public enum ConfigProperties {
    CLIENT_ID("clientId"),
    CLIENT_SECRET("clientSecret"),
    CLIENT_WELCOME_PAGE("client.welcomePage"),
    CLIENT_CALLBACK_URL("client.callback.url"),
    ACCESS_TOKEN_URL("accessTokenUrl"),
    REST_URL("client.connector.rest.url");

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);

    /**
     * Sciezka do pliku z konfiguracja.
     */
    private static final String PATH = "/localConfig.properties";
    /**
     * Klucz property.
     */
    private final String key;

    /**
     * Wartosc property.
     */
    private String value;
    /**
     * Obiekt Properties zaladowany z pliku.
     */
    private static Properties properties;

    ConfigProperties(final String key) {
        this.key = key;
    }

    public String getValue() {
        if (this.value == null) {
            init();
        }
        return this.value;
    }

    private void init() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH));
            } catch (final Exception e) {
                logger.error("Unable to load " + PATH + " file from classpath.", e);
                System.exit(1);
            }
        }
        this.value = (String) properties.get(this.key);
    }
}