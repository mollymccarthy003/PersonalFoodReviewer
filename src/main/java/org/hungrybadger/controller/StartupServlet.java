package org.hungrybadger.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(name = "StartupServlet", urlPatterns = {}, loadOnStartup = 1)
public class StartupServlet extends HttpServlet {

    public static Properties COGNITO_PROPERTIES;

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void init() throws ServletException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("cognito.properties")) {
            if (in == null) {
                logger.error("Cannot find cognito.properties in classpath");
                return;
            }
            COGNITO_PROPERTIES = new Properties();
            COGNITO_PROPERTIES.load(in);

            logger.debug("Cognito properties loaded successfully: CLIENT_ID="
                    + COGNITO_PROPERTIES.getProperty("client.id"));

        } catch (IOException e) {
            logger.error("Error loading cognito.properties", e);
        }
    }
}
