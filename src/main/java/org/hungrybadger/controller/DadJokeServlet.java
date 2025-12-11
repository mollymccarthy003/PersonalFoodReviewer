package org.hungrybadger.controller;

import org.hungrybadger.persistence.DadJokeDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/home")
public class DadJokeServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DadJokeServlet.class);
    private DadJokeDao dadJokeDao;

    @Override
    public void init() {
        dadJokeDao = new DadJokeDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        // Get the date when the joke was last generated
        LocalDate lastDate = (LocalDate) session.getAttribute("jokeDate");
        LocalDate today = LocalDate.now();

        // If first visit or it's a new day, fetch a new joke
        if (lastDate == null || !lastDate.equals(today)) {
            try {
                String joke = dadJokeDao.getRandomJoke();
                session.setAttribute("dadJoke", joke);
                session.setAttribute("jokeDate", today);
            } catch (Exception e) {
                session.setAttribute("dadJoke", "Could not fetch a joke right now");
            }
        }

        // Forward to index.jsp
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
