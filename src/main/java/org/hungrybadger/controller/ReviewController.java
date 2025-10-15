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
        // Initialize DAO for Review
        reviewDao = new GenericDao<>(Review.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Fetch all reviews from the database
        List<Review> reviews = reviewDao.getAll();

        // Set reviews as request attribute for JSP
        request.setAttribute("reviews", reviews);

        // Forward request to reviews.jsp
        request.getRequestDispatcher("/reviews.jsp").forward(request, response);
    }
}
