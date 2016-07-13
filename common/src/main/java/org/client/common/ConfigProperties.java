/**
 * 
 */
package org.client.common;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author piotrek
 *
 */
public enum ConfigProperties {
  ClientId("clientId"), 
  ClientSecret("clientSecret"),
  ClientWelcomePage("client.welcomePage"),
  AccessTokenUrl("accessTokenUrl"),
  RestUrl("client.connector.rest.url");
  /**
   * Logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);

  /**
   * Œcie¿ka do pliku z konfiguracj¹.
   */
  private static final String PATH = "/localConfig.properties";
  /**
   * Klucz property.
   */
  private String key;

  /**
   * Wartoœæ property.
   */
  private String value;
  /**
   * Obiekt Properties za³adowany z pliku.
   */
  private static Properties properties;

  /**
   * 
   * @param key
   *          Klucz property.
   */
  private ConfigProperties(final String key) {
    this.key = key;
  }

  public String getValue() {
    if (value == null) {
      init();
    }
    return value;
  }

  private void init() {
    if (properties == null) {
      properties = new Properties();
      try {
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH)/*ConfigProperties.class.getResourceAsStream(PATH)*/);
      } catch (Exception e) {
        logger.error("Unable to load " + PATH + " file from classpath.", e);
        System.exit(1);
      }
    }
    value = (String) properties.get(this.key);
  }
}