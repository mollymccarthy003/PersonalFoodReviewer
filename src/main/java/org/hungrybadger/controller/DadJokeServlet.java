package org.hungrybadger.controller;

import org.hungrybadger.persistence.DadJokeDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Servlet that handles displaying a random dad joke on the home page.
 * <p>
 * The servlet fetches a new joke from {@link DadJokeDao} once per day and stores it
 * in the user's HTTP session along with the date the joke was generated.
 * Subsequent requests on the same day will reuse the stored joke.
 * </p>
 */
@WebServlet("/home")
public class DadJokeServlet extends HttpServlet {

    /** Logger for this servlet. */
    private static final Logger logger = LogManager.getLogger(DadJokeServlet.class);

    /** DAO used to retrieve dad jokes from the database or API */
    private DadJokeDao dadJokeDao;

    /**
     * Initializes the servlet and sets up the {@link DadJokeDao} instance.
     */
    @Override
    public void init() {
        dadJokeDao = new DadJokeDao();
    }

    /**
     * Handles HTTP GET requests to the home page.
     * <p>
     * Checks the user's session to see if a joke has already been generated for today.
     * If not, fetches a new random dad joke and stores it in the session along with the current date.
     * Finally, forwards the request to {@code index.jsp}.
     * </p>
     *
     * @param request  The {@link HttpServletRequest} object that contains the request the client made.
     * @param response The {@link HttpServletResponse} object that contains the response sent to the client.
     * @throws ServletException If an error occurs while forwarding the request.
     * @throws IOException      If an I/O error occurs during request handling.
     */
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
