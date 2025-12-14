package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.persistence.DadJokeDao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@code StartupServlet} is a {@link ServletContextListener} that initializes
 * application-wide resources when the web application starts.
 * <p>
 * Specifically, it loads Cognito configuration properties used for authentication.
 * The properties are loaded from an environment variable path if set, otherwise
 * from a local resource file (for development/testing).
 * </p>
 * <p>
 * The loaded properties are stored in the public static {@link #COGNITO_PROPERTIES} object,
 * making them accessible throughout the application.
 * </p>
 */
@WebListener
public class StartupServlet implements ServletContextListener {

    /** Logger for logging startup events and errors */
    private static final Logger logger = LogManager.getLogger(StartupServlet.class);

    /** Global properties object containing Cognito configuration */
    public static final Properties COGNITO_PROPERTIES = new Properties();

    /**
     * Called when the web application is starting up.
     * <p>
     * Loads Cognito properties from either:
     * <ul>
     *     <li>An environment variable {@code COGNITO_PROPERTIES_PATH} if set (for cloud deployment)</li>
     *     <li>A local resource file {@code cognito.properties} as a fallback (for local development)</li>
     * </ul>
     * Loaded properties are stored in {@link #COGNITO_PROPERTIES}.
     *
     * @param sce The {@link ServletContextEvent} representing the web application's context.
     */

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try { // Optional: Load from environment variable (useful for cloud deployment)
            String envPath = System.getenv("COGNITO_PROPERTIES_PATH");
            InputStream input;

            if (envPath != null) {
                logger.info("Loading Cognito properties from environment variable: {}", envPath);
                input = new FileInputStream(envPath);
            } else { // Fallback: Load from local resource for development/testing
                logger.info("Loading Cognito properties from local resources");
                input = getClass().getClassLoader().getResourceAsStream("cognito.properties");
            }

            if (input != null) {
                COGNITO_PROPERTIES.load(input);
                logger.info("Cognito properties loaded successfully");
            } else {
                logger.error("Cognito properties file not found!");
            }

        } catch (Exception e) {
            logger.error("Error loading Cognito properties", e);
        }
    }

    /**
     * Called when the web application is shutting down.
     * <p>
     * No special cleanup is required for this servlet, so this method is intentionally left empty.
     * </p>
     *
     * @param sce The {@link ServletContextEvent} representing the web application's context.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No cleanup needed
    }
}
