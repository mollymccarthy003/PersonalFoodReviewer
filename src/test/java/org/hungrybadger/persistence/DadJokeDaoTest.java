package org.hungrybadger.persistence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link DadJokeDao}.
 * <p>
 * This class tests the functionality of fetching a random dad joke
 * from the external API (https://icanhazdadjoke.com/).
 * </p>
 */
class DadJokeDaoTest {

    /**
     * Tests that {@link DadJokeDao#getRandomJoke()} successfully returns
     * a non-null, non-empty joke string.
     * <p>
     * The test performs basic sanity checks:
     * <ul>
     *     <li>Returned string is not null</li>
     *     <li>Returned string is not empty</li>
     *     <li>Returned string contains at least two words</li>
     * </ul>
     * Any exception during fetching is treated as a test failure.
     * </p>
     */
    @Test
    void testGetRandomJoke() {
        DadJokeDao dao = new DadJokeDao();

        try {
            String joke = dao.getRandomJoke();

            // Check that a joke was returned
            assertNotNull(joke, "Joke should not be null");
            assertFalse(joke.isEmpty(), "Joke should not be empty");

            // Basic sanity check that it contains at least a few words
            assertTrue(joke.split(" ").length > 1, "Joke should contain more than one word");

        } catch (Exception e) {
            fail("Fetching joke threw an exception: " + e.getMessage());
        }
    }
}
