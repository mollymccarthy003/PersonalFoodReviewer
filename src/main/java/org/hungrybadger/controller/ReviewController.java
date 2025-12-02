package org.hungrybadger.controller;

import org.hungrybadger.entity.Review;
import org.hungrybadger.persistence.GenericDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/reviews")
public class ReviewController extends HttpServlet {

    private GenericDao<Review> reviewDao;

    @Override
    public void init() throws ServletException {
        reviewDao = new GenericDao<>(Review.class);
    }

    /**
     * Handles GET requests:
     * - /reviews → list all reviews
     * - /reviews?id={id} → view single review details
     * - /reviews?id={id}&action=edit → load review for editing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String actionParam = request.getParameter("action");

        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Review review = reviewDao.getById(id);

            if ("edit".equals(actionParam)) {
                // Forward to review form for editing
                request.setAttribute("review", review);
                request.getRequestDispatcher("/reviewForm.jsp").forward(request, response);
            } else {
                // Forward to review details page
                request.setAttribute("review", review);
                request.getRequestDispatcher("/reviewDetails.jsp").forward(request, response);
            }
        } else {
            // List all reviews
            List<Review> reviews = reviewDao.getAll();
            request.setAttribute("reviews", reviews);
            request.getRequestDispatcher("/reviews.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests:
     * - action=add → add new review
     * - action=edit → update existing review
     * - action=delete → delete review
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "add":
                addReview(request);
                response.sendRedirect(request.getContextPath() + "/reviews");
                break;

            case "edit":
                int editId = Integer.parseInt(request.getParameter("id"));
                updateReview(request, editId);
                response.sendRedirect(request.getContextPath() + "/reviews?id=" + editId);
                break;

            case "delete":
                int deleteId = Integer.parseInt(request.getParameter("id"));
                deleteReview(deleteId);
                response.sendRedirect(request.getContextPath() + "/reviews");
                break;

            default:
                // Unknown action: redirect to list
                response.sendRedirect(request.getContextPath() + "/reviews");
                break;
        }
    }

    // ---------------- Helper Methods ---------------- //

    private void addReview(HttpServletRequest request) {
        Review review = new Review();
        review.setRestaurantName(request.getParameter("restaurantName"));
        review.setCuisineType(request.getParameter("cuisineType"));
        review.setPersonalRating(Integer.parseInt(request.getParameter("personalRating")));
        review.setPersonalNotes(request.getParameter("personalNotes"));

        reviewDao.insert(review);
    }

    private void updateReview(HttpServletRequest request, int id) {
        Review review = reviewDao.getById(id);
        if (review != null) {
            review.setRestaurantName(request.getParameter("restaurantName"));
            review.setCuisineType(request.getParameter("cuisineType"));
            review.setPersonalRating(Integer.parseInt(request.getParameter("personalRating")));
            review.setPersonalNotes(request.getParameter("personalNotes"));

            reviewDao.update(review);
        }
    }

    private void deleteReview(int id) {
        Review review = reviewDao.getById(id);
        if (review != null) {
            reviewDao.delete(review);
        }
    }
}
