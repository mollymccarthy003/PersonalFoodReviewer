package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * StartupServlet initializes application-wide resources when the web application starts.
 * Specifically, it loads Cognito configuration properties for authentication.
 */
@WebListener
public class StartupServlet implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(StartupServlet.class);

    /**
     * Properties object accessible globally across the application.
     */
    public static final Properties COGNITO_PROPERTIES = new Properties();

    /**
     * Called when the web application starts.
     * Loads Cognito properties from environment variable (if set, I have not currently) or local resource as fallback.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Optional: Load from environment variable (useful for cloud deployment)
            String envPath = System.getenv("COGNITO_PROPERTIES_PATH");
            InputStream input;

            if (envPath != null) {
                logger.info("Loading Cognito properties from environment variable: {}", envPath);
                input = new FileInputStream(envPath);
            } else {
                // Fallback: Load from local resource for development/testing
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

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // This method is called when the web application shuts down.
        // Do not need to release any resources manually here,
        // so it is intentionally left empty.
    }
}
