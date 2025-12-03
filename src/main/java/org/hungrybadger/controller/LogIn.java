package org.hungrybadger.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet("/logIn")
public class LogIn extends HttpServlet {

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
