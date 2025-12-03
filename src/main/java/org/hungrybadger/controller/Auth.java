package org.hungrybadger.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hungrybadger.auth.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
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
    private String loginUrl;
    private String redirectUrl;
    private String region;
    private String poolId;

    @Override
    public void init() throws ServletException {
        super.init();
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
        loginUrl = props.getProperty("loginURL");
        redirectUrl = props.getProperty("redirectURL");
        region = props.getProperty("region");
        poolId = props.getProperty("poolId");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        if (code == null) {
            // User not authenticated → redirect to login
            resp.sendRedirect(req.getContextPath() + "/logIn");
            return;
        }

        try {
            // Exchange code for tokens
            HttpRequest tokenRequest = buildAuthRequest(code);
            TokenResponse tokenResponse = getToken(tokenRequest);

            // Validate ID token & extract username
            String username = validate(tokenResponse);

            // Save username in session
            req.getSession().setAttribute("userName", username);

            // Redirect to home page
            resp.sendRedirect(req.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            logger.error("Error during authentication", e);
            resp.sendRedirect(req.getContextPath() + "/error.jsp");
        }
    }

    private HttpRequest buildAuthRequest(String code) {
        String keys = clientId + ":" + clientSecret;

        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("client-secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", redirectUrl);

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

    private TokenResponse getToken(HttpRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), TokenResponse.class);
    }

    private void loadKeys() {
        try {
            URL jwksURL = new URL(String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", region, poolId));
            File file = new File("jwks.json");
            FileUtils.copyURLToFile(jwksURL, file);

            ObjectMapper mapper = new ObjectMapper();
            jwks = mapper.readValue(file, Keys.class);
        } catch (Exception e) {
            logger.error("Error loading JWKS", e);
        }
    }

    private String validate(TokenResponse tokenResponse) throws Exception {
        CognitoTokenHeader header = new ObjectMapper().readValue(
                CognitoJWTParser.getHeader(tokenResponse.getIdToken()).toString(),
                CognitoTokenHeader.class
        );

        BigInteger modulus = new BigInteger(1, Base64.getDecoder().decode(jwks.getKeys().get(0).getN()));
        BigInteger exponent = new BigInteger(1, Base64.getDecoder().decode(jwks.getKeys().get(0).getE()));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new RSAPublicKeySpec(modulus, exponent));

        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);

        String iss = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, poolId);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(iss)
                .withClaim("token_use", "id")
                .build();

        DecodedJWT jwt = verifier.verify(tokenResponse.getIdToken());
        return jwt.getClaim("cognito:username").asString();
    }
}
