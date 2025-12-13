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

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {

    private GenericDao<Review> reviewDao;
    private PhotoDao photoDao;
    private static final Logger logger = LogManager.getLogger(ReviewController.class);

    @Override
    public void init() {
        reviewDao = new GenericDao<>(Review.class);
        photoDao = new PhotoDao();
    }

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
                deleteReview(deleteId, user);
                response.sendRedirect(request.getContextPath() + "/reviews");
                break;

            default:
                logger.warn("Unknown POST action {}", action);
                response.sendRedirect(request.getContextPath() + "/reviews");
        }
    }

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

        reviewDao.insert(review);
        logger.info("Added new review '{}' by user {}", review.getRestaurantName(), user.getEmail());
    }

    private void updateReview(HttpServletRequest request, int id, User user) {
        Review review = reviewDao.getById(id);
        if (review == null || review.getUser() == null || review.getUser().getId() != user.getId()) return;

        review.setRestaurantName(request.getParameter("restaurantName"));
        review.setCuisineType(request.getParameter("cuisineType"));
        review.setPersonalRating(Integer.parseInt(request.getParameter("personalRating")));
        review.setPersonalNotes(request.getParameter("personalNotes"));

        reviewDao.update(review);
    }

    private void deleteReview(int id, User user) {
        Review review = reviewDao.getById(id);
        if (review == null || review.getUser() == null || review.getUser().getId() != user.getId()) return;

        reviewDao.delete(review);
    }
}