package org.hungrybadger.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 * Servlet responsible for initiating the login process with Amazon Cognito.
 * <p>
 * Handles HTTP GET requests to the "/logIn" endpoint by constructing the Cognito
 * authorization URL using properties loaded at startup and redirecting the client
 * to that URL.
 * </p>
 */
@WebServlet("/logIn")
public class LogIn extends HttpServlet {

    /**
     * Handles HTTP GET requests to start the Cognito login flow.
     * <p>
     * The method:
     * <ol>
     *     <li>Retrieves Cognito configuration properties from {@link StartupServlet}.</li>
     *     <li>Builds the authorization URL with response type, client ID, and redirect URI.</li>
     *     <li>Redirects the client to the Cognito login page.</li>
     * </ol>
     * If the Cognito properties are not loaded, it responds with an HTTP 500 error.
     * </p>
     *
     * @param req  The {@link HttpServletRequest} object containing the request from the client.
     * @param resp The {@link HttpServletResponse} object used to send the response to the client.
     * @throws ServletException If an error occurs during request handling.
     * @throws IOException      If an I/O error occurs while sending the redirect.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties props = StartupServlet.COGNITO_PROPERTIES;

        if (props == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cognito properties not loaded");
            return;
        }

        String clientId = props.getProperty("client.id");
        String loginUrl = props.getProperty("loginURL");
        String redirectUrl = props.getProperty("redirectURL");

        String url = loginUrl + "?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUrl;

        resp.sendRedirect(url);
    }
}
