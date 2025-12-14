package org.hungrybadger.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;


/**
 * Interface providing a default method to load properties from a file.
 * <p>
 * Any class implementing this interface can use the {@link #loadProperties(String)} method
 * to load a {@link Properties} object from a given file path.
 * </p>
 * <p>
 * A shared {@link Logger} instance is provided for logging success or errors during
 * properties loading.
 * </p>
 */
public interface PropertiesLoader {

    /**
     * Logger instance shared by all implementing classes.
     */
    Logger logger = LogManager.getLogger(PropertiesLoader.class);

    /**
     * Loads a {@link Properties} object from the specified file path.
     * <p>
     * The properties file should be accessible via the classpath.
     * </p>
     *
     * @param propertiesFilePath the path to the properties file relative to the classpath
     * @return a {@link Properties} object containing key-value pairs from the file
     * @throws IOException if there is an error reading the properties file
     * @throws Exception   if any other unexpected error occurs during loading
     */
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
