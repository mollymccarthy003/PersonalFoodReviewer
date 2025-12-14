package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.entity.Photo;
import org.hungrybadger.persistence.PhotoDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Servlet responsible for deleting a photo associated with a review.
 * <p>
 * This servlet handles HTTP POST requests to delete a photo both from the file system
 * and from the database. It expects a request parameter "photoId" identifying the photo
 * to delete. On success or failure, it sets an appropriate message in the session
 * and redirects back to the review details page.
 * </p>
 */
@WebServlet("/deletePhoto")
public class DeletePhoto extends HttpServlet {

    /** Logger for logging servlet events and errors */
    private static final Logger logger = LogManager.getLogger(DeletePhoto.class);

    /** DAO used to access and manipulate Photo entities */
    private PhotoDao photoDao;

    /**
     * Initializes the servlet and sets up the {@link PhotoDao}.
     * Logs initialization for debugging purposes.
     */
    @Override
    public void init() {
        photoDao = new PhotoDao();
        logger.info("PhotoDeleteServlet initialized.");
    }

    /**
     * Handles HTTP POST requests to delete a photo.
     * <p>
     * The method:
     * <ol>
     *     <li>Validates and parses the "photoId" request parameter.</li>
     *     <li>Retrieves the corresponding {@link Photo} entity from the database.</li>
     *     <li>Deletes the photo file from the server disk if it exists.</li>
     *     <li>Deletes the photo entity from the database.</li>
     *     <li>Sets success or error messages in the HTTP session.</li>
     *     <li>Redirects the user back to the review details page.</li>
     * </ol>
     *
     * @param req  The {@link HttpServletRequest} object that contains the request made by the client.
     * @param resp The {@link HttpServletResponse} object used to return the response to the client.
     * @throws ServletException If an error occurs during request handling.
     * @throws IOException      If an I/O error occurs while sending the redirect or accessing the file.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int photoId;
        try {
            photoId = Integer.parseInt(req.getParameter("photoId"));
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid photo ID.");
            resp.sendRedirect(req.getContextPath() + "/reviews");
            return;
        }

        Photo photo = photoDao.getById(photoId);
        if (photo == null) {
            req.getSession().setAttribute("errorMessage", "Photo not found.");
            resp.sendRedirect(req.getContextPath() + "/reviews");
            return;
        }

        boolean fileDeleted = false;
        try {
            // Delete the file from disk
            String filePath = getServletContext().getRealPath("/" + photo.getImagePath());
            File file = new File(filePath);
            if (file.exists()) {
                fileDeleted = file.delete();
                logger.info("Deleted file {}: {}", filePath, fileDeleted);
            }

            // Delete the Photo entity from DB
            photoDao.delete(photo);
            logger.info("Deleted photo entity with ID {}", photoId);

            // Set success message
            req.getSession().setAttribute("successMessage", "Photo deleted successfully!");

        } catch (Exception e) {
            logger.error("Error deleting photo ID {}", photoId, e);
            req.getSession().setAttribute("errorMessage", "Failed to delete photo. Please try again.");
        }

        // Redirect back to the review details page
        int reviewId = photo.getReview().getId();
        resp.sendRedirect(req.getContextPath() + "/reviews?id=" + reviewId);
    }
}