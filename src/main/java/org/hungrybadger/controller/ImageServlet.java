package org.hungrybadger.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Servlet for serving uploaded images to clients.
 * <p>
 * This servlet handles HTTP GET requests to URLs matching "/uploads/*".
 * It reads the requested image file from the local upload directory and writes
 * it to the HTTP response output stream. If the file does not exist, it returns
 * a 404 Not Found error.
 * </p>
 */
@WebServlet("/uploads/*")
public class ImageServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for uploaded images.
     * <p>
     * The method extracts the file name from the request path, locates the file
     * in the upload directory, sets the appropriate MIME type, and writes the file
     * to the response output stream. If the file is not found, it sends a 404 error.
     * </p>
     *
     * @param req  The {@link HttpServletRequest} object containing the request from the client.
     * @param resp The {@link HttpServletResponse} object used to send the response to the client.
     * @throws IOException If an I/O error occurs while reading the file or writing to the response.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fileName = req.getPathInfo().substring(1); // remove leading slash
        File file = new File("C:/PersonalFoodReviewerUploads", fileName);

        if (file.exists()) {
            resp.setContentType(getServletContext().getMimeType(file.getName()));
            Files.copy(file.toPath(), resp.getOutputStream());
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}