package org.hungrybadger.persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Data Access Object (DAO) for fetching dad jokes from the
 * <a href="https://icanhazdadjoke.com/">icanhazdadjoke API</a>.
 * <p>
 * This class handles HTTP requests to the API and processes the JSON response
 * to extract a single joke string. It also decodes Unicode escape sequences.
 * </p>
 */
public class DadJokeDao {

    /** Base URL for the icanhazdadjoke API. */
    private static final String API_URL = "https://icanhazdadjoke.com/";

    /**
     * Converts Unicode escape sequences in the input string (e.g., \u2019)
     * into the corresponding characters.
     *
     * @param input the string containing Unicode escape sequences
     * @return the decoded string with real Unicode characters
     */
    private String unescapeUnicode(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);
            if (c == '\\' && i + 5 < input.length() && input.charAt(i + 1) == 'u') {
                String hex = input.substring(i + 2, i + 6);
                try {
                    int code = Integer.parseInt(hex, 16);
                    result.append((char) code);
                    i += 6;
                    continue;
                } catch (NumberFormatException e) {
                    // fallback: not a valid unicode escape
                }
            }
            result.append(c);
            i++;
        }

        return result.toString();
    }

    /**
     * Fetches a random dad joke from the icanhazdadjoke API.
     * <p>
     * This method sends an HTTP GET request with Accept: application/json
     * and parses the JSON response to extract the joke text. It also decodes
     * escape sequences and Unicode characters.
     * </p>
     *
     * @return a randomly generated dad joke as a string
     * @throws Exception if there is an error connecting to the API or parsing the response
     */
    public String getRandomJoke() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int status = conn.getResponseCode();
        if (status != 200) {
            conn.disconnect();
            throw new RuntimeException("HTTP error: " + status);
        }

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString();

            // Extract text between "joke":" and the next quote
            int start = json.indexOf("\"joke\":\"") + 8;
            int end = json.indexOf("\"", start);
            String joke = json.substring(start, end);

            // Unescape \", \\n, etc.
            joke = joke.replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\\\", "\\");

            // Decode unicode escapes like \u2019
            joke = unescapeUnicode(joke);

            return joke;
        } finally {
            conn.disconnect();
        }
    }

}