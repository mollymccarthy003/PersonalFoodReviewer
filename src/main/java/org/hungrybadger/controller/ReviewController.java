package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.entity.Photo;
import org.hungrybadger.entity.Review;
import org.hungrybadger.entity.User;
import org.hungrybadger.persistence.GenericDao;
import org.hungrybadger.persistence.PhotoDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet that manages CRUD operations for {@link Review} entities.
 * <p>
 * Handles HTTP GET requests to display a single review, a list of reviews for the
 * logged-in user, or to forward to the review edit form. Handles HTTP POST requests
 * to add, edit, or delete reviews.
 * </p>
 */
@WebServlet("/reviews")
public class ReviewController extends HttpServlet {

    /** Logger for logging servlet events and errors */
    private static final Logger logger = LogManager.getLogger(ReviewController.class);

    /** DAO for managing Review entities */
    private GenericDao<Review> reviewDao;

    /** DAO for managing Photo entities */
    private PhotoDao photoDao;

    /**
     * Initializes the servlet and sets up DAOs for reviews and photos.
     */
    @Override
    public void init() {
        reviewDao = new GenericDao<>(Review.class);
        photoDao = new PhotoDao();
    }

    /**
     * Handles HTTP GET requests.
     * <p>
     * The method supports the following:
     * <ul>
     *     <li>Viewing a single review with its associated photos.</li>
     *     <li>Forwarding to the review edit form.</li>
     *     <li>Listing all reviews for the logged-in user.</li>
     * </ul>
     * Ensures the user is logged in and authorized to access the requested review.
     * </p>
     *
     * @param request  The {@link HttpServletRequest} object containing the request from the client.
     * @param response The {@link HttpServletResponse} object used to send the response to the client.
     * @throws ServletException If an error occurs during request handling or forwarding.
     * @throws IOException      If an I/O error occurs during redirect or forwarding.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("GET /reviews");

        User user = getLoggedInUser(request, response);
        if (user == null) return;

        String idParam = request.getParameter("id");
        String actionParam = request.getParameter("action");

        if (idParam != null) {
            try {
                int reviewId = Integer.parseInt(idParam);
                Review review = reviewDao.getById(reviewId);

                if (review == null || review.getUser() == null || review.getUser().getId() != user.getId()) {
                    logger.warn("Unauthorized access attempt to review {}", reviewId);
                    response.sendRedirect(request.getContextPath() + "/reviews");
                    return;
                }

                request.setAttribute("review", review);

                // Load photos for this review
                List<Photo> photos = photoDao.getByReview(reviewId);
                request.setAttribute("photos", photos);

                if ("edit".equals(actionParam)) {
                    request.getRequestDispatcher("/reviewForm.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/reviewDetails.jsp").forward(request, response);
                }
                return;

            } catch (NumberFormatException e) {
                logger.error("Invalid review ID: {}", idParam, e);
                response.sendRedirect(request.getContextPath() + "/reviews");
                return;
            }
        }

        // List all reviews for the logged-in user
        List<Review> reviews = reviewDao.getByPropertyEqual("user", user);
        if (reviews == null) {
            reviews = List.of(); // Avoid null pointer in JSP
        }
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/reviews.jsp").forward(request, response);
    }


    /**
     * Handles HTTP POST requests to add, edit, or delete a review.
     * <p>
     * The "action" request parameter determines the operation:
     * <ul>
     *     <li>"add" – adds a new review.</li>
     *     <li>"edit" – updates an existing review.</li>
     *     <li>"delete" – deletes an existing review.</li>
     * </ul>
     * Ensures the logged-in user is authorized for the operation.
     * </p>
     *
     * @param request  The {@link HttpServletRequest} object containing the request from the client.
     * @param response The {@link HttpServletResponse} object used to send the response to the client.
     * @throws ServletException If an error occurs during request handling.
     * @throws IOException      If an I/O error occurs during redirect.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request, response);
        if (user == null) return;

        String action = request.getParameter("action");
        if (action == null) action = "add";

        switch (action) {
            case "add":
                addReview(request, user);
                response.sendRedirect(request.getContextPath() + "/reviews");
                break;

            case "edit":
                int editId = Integer.parseInt(request.getParameter("id"));
                updateReview(request, editId, user);
                response.sendRedirect(request.getContextPath() + "/reviews?id=" + editId);
                break;

            case "delete":
                int deleteId = Integer.parseInt(request.getParameter("id"));
                boolean deleted = deleteReview(deleteId, user); // returns true/false
                if (deleted) {
                    request.getSession().setAttribute("successMessage", "Review deleted successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to delete review.");
                }
                response.sendRedirect(request.getContextPath() + "/reviews");
                break;

            default:
                logger.warn("Unknown POST action {}", action);
                response.sendRedirect(request.getContextPath() + "/reviews");
        }
    }

    /**
     * Retrieves the currently logged-in user from the session.
     * <p>
     * Redirects to "/auth" if the session or user object is missing.
     * </p>
     *
     * @param request  The {@link HttpServletRequest} object containing the request.
     * @param response The {@link HttpServletResponse} object used for redirect if not logged in.
     * @return The {@link User} object for the logged-in user, or {@code null} if not authenticated.
     * @throws IOException If an I/O error occurs during redirect.
     */
    private User getLoggedInUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("No session found, redirecting to /auth");
            response.sendRedirect(request.getContextPath() + "/auth");
            return null;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            logger.warn("No user object in session, redirecting to /auth");
            response.sendRedirect(request.getContextPath() + "/auth");
            return null;
        }

        logger.info("Authenticated user: {} ({})", user.getFullName(), user.getEmail());
        return user;
    }

    /**
     * Adds a new review for the specified user using parameters from the request.
     *
     * @param request The {@link HttpServletRequest} containing review data.
     * @param user    The {@link User} who owns the new review.
     */
    private void addReview(HttpServletRequest request, User user) {
        Review review = new Review();
        String restaurantName = request.getParameter("restaurantName");
        review.setRestaurantName(restaurantName != null ? restaurantName.trim() : "");
        String cuisineType = request.getParameter("cuisineType");
        review.setCuisineType(cuisineType != null ? cuisineType.trim() : "");

        int rating = 1;
        String ratingParam = request.getParameter("personalRating");
        if (ratingParam != null && !ratingParam.isEmpty()) {
            try {
                rating = Integer.parseInt(ratingParam);
                if (rating < 1) rating = 1;
                if (rating > 5) rating = 5;
            } catch (NumberFormatException e) {
                logger.warn("Invalid personalRating '{}', defaulting to 1", ratingParam);
                rating = 1;
            }
        }
        review.setPersonalRating(rating);

        String notes = request.getParameter("personalNotes");
        review.setPersonalNotes(notes != null ? notes.trim() : "");
        review.setUser(user);

        try {
            reviewDao.insert(review);
            request.getSession().setAttribute("successMessage", "Review added successfully!");
            logger.info("Added new review '{}' by user {}", review.getRestaurantName(), user.getEmail());
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Failed to add review. Please try again.");
            logger.error("Error adding review", e);
        }
    }

    /**
     * Updates an existing review with new data from the request.
     * <p>
     * Ensures the review exists and the logged-in user is authorized to update it.
     * </p>
     *
     * @param request The {@link HttpServletRequest} containing updated review data.
     * @param id      The ID of the review to update.
     * @param user    The {@link User} performing the update.
     */
    private void updateReview(HttpServletRequest request, int id, User user) {
        Review review = reviewDao.getById(id);
        if (review == null || review.getUser() == null || review.getUser().getId() != user.getId()) {
            request.getSession().setAttribute("errorMessage", "Review not found or unauthorized.");
            return;
        }

        try {
            review.setRestaurantName(request.getParameter("restaurantName"));
            review.setCuisineType(request.getParameter("cuisineType"));
            review.setPersonalRating(Integer.parseInt(request.getParameter("personalRating")));
            review.setPersonalNotes(request.getParameter("personalNotes"));

            reviewDao.update(review);
            request.getSession().setAttribute("successMessage", "Review updated successfully!");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Failed to update review. Please try again.");
            logger.error("Error updating review", e);
        }
    }


    /**
     * Deletes the specified review if the logged-in user is authorized.
     *
     * @param id   The ID of the review to delete.
     * @param user The {@link User} attempting the deletion.
     * @return {@code true} if the review was successfully deleted; {@code false} otherwise.
     */
    private boolean deleteReview(int id, User user) {
        Review review = reviewDao.getById(id);

        if (review == null || review.getUser() == null || review.getUser().getId() != user.getId()) {
            logger.warn("Unauthorized or nonexistent review deletion attempt: reviewId={}, userId={}", id, user.getId());
            return false;
        }

        try {
            reviewDao.delete(review);
            logger.info("Deleted review '{}' by user {}", review.getRestaurantName(), user.getEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error deleting review: reviewId={}", id, e);
            return false;
        }
    }

}