package org.hungrybadger.persistence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DadJokeDaoTest {

    @Test
    void testGetRandomJoke() {
        DadJokeDao dao = new DadJokeDao();

        try {
            String joke = dao.getRandomJoke();

            // Check that a joke was returned
            assertNotNull(joke, "Joke should not be null");
            assertFalse(joke.isEmpty(), "Joke should not be empty");

            // Optional: basic sanity check that it contains at least a few words
            assertTrue(joke.split(" ").length > 1, "Joke should contain more than one word");

        } catch (Exception e) {
            fail("Fetching joke threw an exception: " + e.getMessage());
        }
    }
}
