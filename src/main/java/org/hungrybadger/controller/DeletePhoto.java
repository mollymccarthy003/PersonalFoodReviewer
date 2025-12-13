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

@WebServlet("/deletePhoto")
public class DeletePhoto extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DeletePhoto.class);
    private PhotoDao photoDao;

    @Override
    public void init() {
        photoDao = new PhotoDao();
        logger.info("PhotoDeleteServlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int photoId;
        try {
            photoId = Integer.parseInt(req.getParameter("photoId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid photo ID");
            return;
        }

        Photo photo = photoDao.getById(photoId);
        if (photo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo not found");
            return;
        }

        // 1. Delete the file from disk
        String filePath = getServletContext().getRealPath("/" + photo.getImagePath());
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            logger.info("Deleted file {}: {}", filePath, deleted);
        }

        // 2. Delete the Photo entity from DB
        photoDao.delete(photo);
        logger.info("Deleted photo entity with ID {}", photoId);

        // 3. Redirect back to the review details page
        int reviewId = photo.getReview().getId();
        resp.sendRedirect(req.getContextPath() + "/reviews?id=" + reviewId);
    }
}
