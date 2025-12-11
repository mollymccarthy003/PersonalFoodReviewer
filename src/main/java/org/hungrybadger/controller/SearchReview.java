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

        // Get logged-in user from session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // redirect to login page if not logged in
            resp.sendRedirect(req.getContextPath() + "/auth");
            return;
        }

        User user = (User) session.getAttribute("user");

        String searchTerm = req.getParameter("searchTerm");
        String submit = req.getParameter("submit"); // "search" button

        logger.debug("submit param: {}", submit);
        logger.debug("search term: {}", searchTerm);

        List<Review> results;

        if ("search".equals(submit) && searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Search by restaurantName or cuisineType for this user
            List<Review> byRestaurant = reviewDao.getByPropertyLike("restaurantName", searchTerm);
            List<Review> byCuisine = reviewDao.getByPropertyLike("cuisineType", searchTerm);

            // Combine results
            results = new ArrayList<>();
            for (Review r : byRestaurant) {
                if (r.getUser().getId() == user.getId()) results.add(r);
            }
            for (Review r : byCuisine) {
                if (r.getUser().getId() == user.getId() && !results.contains(r)) results.add(r);
            }
        } else {
            // default = only show this user's reviews
            results = reviewDao.getByPropertyEqual("user", user);
        }

        req.setAttribute("reviews", results);
        req.getRequestDispatcher("/searchResults.jsp").forward(req, resp);
    }

}