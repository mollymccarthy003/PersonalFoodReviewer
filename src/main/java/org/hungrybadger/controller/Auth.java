package org.hungrybadger.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.auth.Keys;
import org.hungrybadger.auth.TokenResponse;
import org.hungrybadger.entity.User;
import org.hungrybadger.persistence.GenericDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Servlet for handling authentication with AWS Cognito.
 * <p>
 * Supports OAuth2 login flow, retrieves Cognito tokens, and manages user sessions.
 * </p>
 */
@WebServlet("/auth")
public class Auth extends HttpServlet {

    /** Logger for this servlet. */
    private final Logger logger = LogManager.getLogger(this.getClass());

    /** DAO for accessing User entities in the database. */
    private GenericDao<User> userDao = new GenericDao<>(User.class);

    /** Holds Cognito JSON Web Keys (JWKs) for token verification. */
    private Keys jwks;

    /** Cognito client ID. */
    private String clientId;

    /** Cognito client secret. */
    private String clientSecret;

    /** OAuth URL for Cognito. */
    private String oauthUrl;

    /** AWS region of the Cognito user pool. */
    private String region;

    /** Cognito user pool ID. */
    private String poolId;

    /**
     * Initializes the servlet, loads properties and JWKS keys.
     *
     * @throws ServletException if an initialization error occurs
     */
    @Override
    public void init() throws ServletException {
        super.init();
        loadProperties();
        loadKeys();
    }

    /**
     * Loads Cognito configuration properties from the StartupServlet.
     */
    private void loadProperties() {
        Properties props = StartupServlet.COGNITO_PROPERTIES;
        clientId = props.getProperty("client.id");
        clientSecret = props.getProperty("client.secret");
        oauthUrl = props.getProperty("oauthURL");
        region = props.getProperty("region");
        poolId = props.getProperty("poolId");
    }

    /**
     * Handles GET requests for the OAuth2 login flow.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (code == null && user == null) {
            resp.sendRedirect(req.getContextPath() + "/logIn");
            return;
        }

        try {
            //Hardcoded prod redirect URI
            String redirectUri = "https://" + req.getServerName() + req.getContextPath() + "/auth";
            // Hardcoded local dev redirect URI
            // String redirectUri = "http://localhost:8080/PersonalFoodReviewer/auth";


            logger.info("Redirect URI: {}", redirectUri);

            HttpRequest tokenRequest = buildAuthRequest(code, redirectUri);
            TokenResponse tokenResponse = getToken(tokenRequest);
            DecodedJWT jwt = validate(tokenResponse);

            String fullName = jwt.getClaim("name").asString();
            String email = jwt.getClaim("email").asString();
            String cognitoSub = jwt.getSubject();

            List<User> existingUsers = userDao.getByPropertyEqual("cognitoSub", cognitoSub);
            if (existingUsers.isEmpty()) {
                user = new User();
                user.setFullName(fullName);
                user.setEmail(email);
                user.setCognitoSub(cognitoSub);
                userDao.insert(user);
                logger.info("Created new DB user: {}", email);
            } else {
                user = existingUsers.get(0);
            }

            session = req.getSession(true);
            session.setAttribute("user", user);

            resp.sendRedirect(req.getContextPath() + "/reviews");

        } catch (Exception e) {
            logger.error("Auth error", e);
            resp.sendRedirect(req.getContextPath() + "/logIn?error=auth");
        }
    }


    /**
     * Builds an HTTP POST request to obtain an OAuth token using the authorization code flow.
     *
     * @param code The authorization code received from the OAuth provider after user login.
     * @param redirectUri The redirect URI registered with the OAuth provider.
     * @return An {@link HttpRequest} configured with form parameters and Basic Authentication header.
     */
    private HttpRequest buildAuthRequest(String code, String redirectUri) {
        String keys = clientId + ":" + clientSecret;
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", redirectUri);

        String form = params.keySet().stream()
                .map(k -> k + "=" + URLEncoder.encode(params.get(k), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        String basicAuth = Base64.getEncoder().encodeToString(keys.getBytes());

        return HttpRequest.newBuilder()
                .uri(URI.create(oauthUrl))
                .headers("Content-Type", "application/x-www-form-urlencoded",
                        "Authorization", "Basic " + basicAuth)
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
    }

    /**
     * Sends an HTTP request to the OAuth provider and parses the response into a {@link TokenResponse}.
     *
     * @param request The {@link HttpRequest} object containing the OAuth token request.
     * @return A {@link TokenResponse} object representing the parsed response.
     * @throws IOException If an I/O error occurs when sending or receiving the request.
     * @throws InterruptedException If the operation is interrupted while waiting for the response.
     */
    private TokenResponse getToken(HttpRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), TokenResponse.class);
    }

    /**
     * Loads the JSON Web Key Set (JWKS) from the Cognito endpoint for the configured user pool.
     * The JWKS is used later for validating JWT tokens.
     */
    private void loadKeys() {
        try {
            String jwksUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", region, poolId);
            ObjectMapper mapper = new ObjectMapper();
            jwks = mapper.readValue(new URI(jwksUrl).toURL(), Keys.class);
        } catch (Exception e) {
            logger.error("JWKS load error", e);
        }
    }

    /**
     * Validates a JWT ID token using the first key from the loaded JWKS.
     * Ensures the token is issued by the correct Cognito user pool and has the expected claims.
     *
     * @param tokenResponse The {@link TokenResponse} containing the ID token to validate.
     * @return A {@link DecodedJWT} representing the verified and decoded token.
     * @throws Exception If an error occurs while constructing the public key or verifying the token.
     */
    private DecodedJWT validate(TokenResponse tokenResponse) throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new RSAPublicKeySpec(
                        new BigInteger(1, Base64.getUrlDecoder().decode(jwks.getKeys().get(0).getN())),
                        new BigInteger(1, Base64.getUrlDecoder().decode(jwks.getKeys().get(0).getE()))
                ));

        return JWT.require(com.auth0.jwt.algorithms.Algorithm.RSA256(publicKey, null))
                .withIssuer(String.format("https://cognito-idp.%s.amazonaws.com/%s", region, poolId))
                .withClaim("token_use", "id")
                .build()
                .verify(tokenResponse.getIdToken());
    }
}
