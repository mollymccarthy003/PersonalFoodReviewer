package org.hungrybadger.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public interface PropertiesLoader {

    // Create a logger instance (each implementing class will share this)
    Logger logger = LogManager.getLogger(PropertiesLoader.class);

    default Properties loadProperties(String propertiesFilePath) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream(propertiesFilePath));
            logger.info("Successfully loaded properties from {}", propertiesFilePath);
        } catch (IOException ioException) {
            logger.error("Error reading properties file: {}", propertiesFilePath, ioException);
            throw ioException;
        } catch (Exception exception) {
            logger.error("Unexpected error loading properties file: {}", propertiesFilePath, exception);
            throw exception;
        }
        return properties;
    }
}
