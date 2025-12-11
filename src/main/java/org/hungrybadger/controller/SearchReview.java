package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.entity.Review;
import org.hungrybadger.entity.User;
import org.hungrybadger.persistence.GenericDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "searchReview",
        urlPatterns = {"/searchReview"}
)
public class SearchReview extends HttpServlet {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private GenericDao<Review> reviewDao;

    @Override
    public void init() {
        reviewDao = new GenericDao<>(Review.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warn("User not logged in. Redirecting to /auth");
            resp.sendRedirect(req.getContextPath() + "/auth");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Get search term from form
        String searchTerm = req.getParameter("searchTerm");
        logger.info("User {} ({}) searching for term: '{}'", user.getFullName(), user.getEmail(), searchTerm);

        List<Review> results = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Fetch reviews by restaurantName or cuisineType
            List<Review> byRestaurant = reviewDao.getByPropertyLike("restaurantName", searchTerm);
            List<Review> byCuisine = reviewDao.getByPropertyLike("cuisineType", searchTerm);

            logger.debug("Found {} reviews matching restaurant name", byRestaurant.size());
            logger.debug("Found {} reviews matching cuisine type", byCuisine.size());

            // Filter by logged-in user
            for (Review r : byRestaurant) {
                if (r.getUser() != null && r.getUser().getId() == user.getId()) {
                    results.add(r);
                    logger.debug("Adding restaurant match: {} (ID {})", r.getRestaurantName(), r.getId());
                }
            }
            for (Review r : byCuisine) {
                if (r.getUser() != null && r.getUser().getId() == user.getId() && !results.contains(r)) {
                    results.add(r);
                    logger.debug("Adding cuisine match: {} (ID {})", r.getRestaurantName(), r.getId());
                }
            }
        } else {
            // No search term: show only this user's reviews
            results = reviewDao.getByPropertyEqual("user", user);
            logger.debug("No search term provided. Returning {} reviews for user.", results.size());
        }

        logger.info("Total search results for user {}: {}", user.getFullName(), results.size());
        req.setAttribute("reviews", results);

        // Forward to JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/searchResults.jsp");
        dispatcher.forward(req, resp);
    }
}