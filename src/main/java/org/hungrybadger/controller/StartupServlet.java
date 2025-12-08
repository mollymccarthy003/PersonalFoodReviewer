package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class StartupServlet implements ServletContextListener {

    private final Logger logger = LogManager.getLogger(this.getClass());

    // Make properties globally accessible
    public static Properties COGNITO_PROPERTIES = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Try to load from environment variable first (Elastic Beanstalk)
            String envPath = System.getenv("COGNITO_PROPERTIES_PATH");
            InputStream input;

            if (envPath != null) {
                logger.info("Loading Cognito properties from environment: " + envPath);
                input = new FileInputStream(envPath);
            } else {
                // Fallback to local file in src/main/resources
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
        // nothing to clean up
    }
}
