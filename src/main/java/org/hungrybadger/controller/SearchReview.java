package org.hungrybadger.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.entity.Review;
import org.hungrybadger.persistence.GenericDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        String searchTerm = req.getParameter("searchTerm");
        String submit = req.getParameter("submit"); // "search" button

        logger.debug("submit param: {}", submit);
        logger.debug("search term: {}", searchTerm);

        if ("search".equals(submit) && searchTerm != null && !searchTerm.trim().isEmpty()) {
            // PARTIAL MATCH search
            req.setAttribute("reviews",
                    reviewDao.getByPropertyLike("title", searchTerm));
        } else {
            // default = show all
            req.setAttribute("reviews", reviewDao.getAll());
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/searchResults.jsp");
        dispatcher.forward(req, resp);
    }
}
