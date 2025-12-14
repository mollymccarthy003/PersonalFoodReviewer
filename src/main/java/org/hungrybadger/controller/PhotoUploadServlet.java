package org.hungrybadger.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.entity.Review;
import org.hungrybadger.persistence.GenericDao;
import org.hungrybadger.entity.Photo;
import org.hungrybadger.persistence.PhotoDao;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Servlet responsible for handling photo uploads associated with reviews.
 * <p>
 * This servlet handles HTTP POST requests to "/uploadPhoto" and allows clients
 * to upload image files. Uploaded files are saved to the server's file system
 * and a corresponding {@link Photo} entity is created and associated with the
 * specified {@link Review}.
 * </p>
 */
@WebServlet("/uploadPhoto")
@MultipartConfig
public class PhotoUploadServlet extends HttpServlet {

    /** Logger for logging servlet events and errors */
    private static final Logger logger = LogManager.getLogger(PhotoUploadServlet.class);

    /** DAO used to access and manipulate Photo entities */
    private PhotoDao photoDao;

    /** Upload folder path in production environment */
    private static final String UPLOAD_DIR = "/var/app/current/uploads";

    /** Upload folder path in local development environment */
    private static final String LOCAL_UPLOAD_DIR = "C:/PersonalFoodReviewerUploads";

    /**
     * Initializes the servlet and sets up the {@link PhotoDao}.
     * Logs initialization for debugging purposes.
     */
    @Override
    public void init() {
        photoDao = new PhotoDao();
        logger.info("PhotoUploadServlet initialized.");
    }

    /**
     * Handles HTTP POST requests to upload a photo for a review.
     * <p>
     * The method performs the following steps:
     * <ol>
     *     <li>Parses the "reviewId" request parameter.</li>
     *     <li>Validates the uploaded file.</li>
     *     <li>Generates a unique filename and determines the upload folder path based on environment.</li>
     *     <li>Saves the uploaded file to the file system.</li>
     *     <li>Loads the corresponding {@link Review} entity.</li>
     *     <li>Creates a {@link Photo} entity, sets its review and image path, and inserts it into the database.</li>
     *     <li>Redirects the client back to the review details page.</li>
     * </ol>
     * Appropriate error responses are sent if any step fails.
     * </p>
     *
     * @param req  The {@link HttpServletRequest} object containing the request from the client.
     * @param resp The {@link HttpServletResponse} object used to send the response to the client.
     * @throws ServletException If an error occurs during request handling.
     * @throws IOException      If an I/O error occurs while saving the file or sending the redirect.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("Photo upload request received.");

        // 1. Get review ID
        int reviewId;
        try {
            reviewId = Integer.parseInt(req.getParameter("reviewId"));
            logger.debug("Review ID: {}", reviewId);
        } catch (NumberFormatException e) {
            logger.error("Invalid review ID: {}", req.getParameter("reviewId"));
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid review ID");
            return;
        }

        // 2. Get uploaded file
        Part filePart = req.getPart("photo");
        if (filePart == null || filePart.getSize() == 0) {
            logger.error("No file uploaded.");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file uploaded");
            return;
        }
        logger.debug("File uploaded: {} ({} bytes)", filePart.getSubmittedFileName(), filePart.getSize());

        // 3. Generate unique filename
        String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
        logger.debug("Generated filename: {}", fileName);

        // 4. Determine folder path based on environment
        String uploadDir = System.getenv("AWS_EXECUTION_ENV") != null ? UPLOAD_DIR : LOCAL_UPLOAD_DIR;
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            logger.info("Upload folder created: {} - {}", folder.getAbsolutePath(), created);
        } else {
            logger.debug("Upload folder exists: {}", folder.getAbsolutePath());
        }

        // 5. Save uploaded file
        File file = new File(folder, fileName);
        Files.copy(filePart.getInputStream(), file.toPath());
        logger.info("File saved at: {}", file.getAbsolutePath());

        // 6. Load Review entity
        GenericDao<Review> reviewDao = new GenericDao<>(Review.class);
        Review review = reviewDao.getById(reviewId);
        if (review == null) {
            logger.error("Review not found for ID: {}", reviewId);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Review not found");
            return;
        }

        // 7. Create Photo entity
        Photo photo = new Photo();
        photo.setReview(review);  // Important: set review
        photo.setImagePath(fileName); // store filename only
        photoDao.insert(photo);
        logger.info("Photo entity inserted into DB with ID: {}", photo.getId());

        // 8. Redirect back to review page
        resp.sendRedirect(req.getContextPath() + "/reviews?id=" + reviewId);
        logger.info("Redirected to review details page for review ID: {}", reviewId);
    }
}
