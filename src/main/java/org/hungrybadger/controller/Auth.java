package org.hungrybadger.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.auth.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;

@WebServlet("/auth")
public class Auth extends HttpServlet {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private Keys jwks;

    private String clientId;
    private String clientSecret;
    private String oauthUrl;
    private String region;
    private String poolId;

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("Initializing Auth servlet");
        loadProperties();
        loadKeys();
    }

    private void loadProperties() {
        Properties props = StartupServlet.COGNITO_PROPERTIES;

        if (props == null) {
            logger.error("Cognito properties not loaded from StartupServlet");
            return;
        }

        clientId = props.getProperty("client.id");
        clientSecret = props.getProperty("client.secret");
        oauthUrl = props.getProperty("oauthURL");
        region = props.getProperty("region");
        poolId = props.getProperty("poolId");

        logger.info("Loaded Cognito properties: clientId={}, region={}, poolId={}", clientId, region, poolId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        if (code == null) {
            logger.warn("No code parameter found; redirecting to login page");
            resp.sendRedirect(req.getContextPath() + "/logIn");
            return;
        }

        try {
            String redirectUri = req.getScheme() + "://" +
                    req.getServerName() +
                    (req.getServerPort() == 80 || req.getServerPort() == 443 ? "" : ":" + req.getServerPort()) +
                    req.getContextPath() + "/auth";

            HttpRequest tokenRequest = buildAuthRequest(code, redirectUri);
            TokenResponse tokenResponse = getToken(tokenRequest);

            // Validate token and extract display name
            String displayName = validate(tokenResponse);

            // Set session attribute
            req.getSession().setAttribute("userName", displayName);
            logger.info("Session attribute set: userName={}", displayName);

            resp.sendRedirect(req.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            logger.error("Error during authentication", e);
            resp.sendRedirect(req.getContextPath() + "/error.jsp");
        }
    }

    private HttpRequest buildAuthRequest(String code, String redirectUri) {
        String keys = clientId + ":" + clientSecret;

        // Original style using keySet() and params.get(k)
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

        logger.info("Built form for token request: {}", form);

        return HttpRequest.newBuilder()
                .uri(URI.create(oauthUrl))
                .headers("Content-Type", "application/x-www-form-urlencoded",
                        "Authorization", "Basic " + basicAuth)
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
    }

    private TokenResponse getToken(HttpRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), TokenResponse.class);
    }

    private void loadKeys() {
        try {
            String jwksUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", region, poolId);
            ObjectMapper mapper = new ObjectMapper();
            jwks = mapper.readValue(new URI(jwksUrl).toURL(), Keys.class);
            logger.info("JWKS loaded successfully: {} keys", jwks.getKeys().size());
        } catch (Exception e) {
            logger.error("Error loading JWKS", e);
        }
    }

    private String validate(TokenResponse tokenResponse) throws Exception {
        DecodedJWT jwt = JWT.require(Algorithm.RSA256(
                        (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(
                                new RSAPublicKeySpec(
                                        new BigInteger(1, Base64.getUrlDecoder().decode(jwks.getKeys().get(0).getN())),
                                        new BigInteger(1, Base64.getUrlDecoder().decode(jwks.getKeys().get(0).getE()))
                                )
                        ),
                        null
                ))
                .withIssuer(String.format("https://cognito-idp.%s.amazonaws.com/%s", region, poolId))
                .withClaim("token_use", "id")
                .build()
                .verify(tokenResponse.getIdToken());

        // Extract display name with fallback: name → email → username
        String displayName = jwt.getClaim("name").asString();
        if (displayName == null || displayName.isEmpty()) {
            displayName = jwt.getClaim("email").asString();
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = jwt.getClaim("cognito:username").asString();
        }

        logger.info("Display name from JWT with fallback: {}", displayName);
        return displayName;
    }
}
