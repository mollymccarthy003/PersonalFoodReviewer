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

@WebServlet("/auth")
public class Auth extends HttpServlet {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private GenericDao<User> userDao = new GenericDao<>(User.class);
    private Keys jwks;

    private String clientId;
    private String clientSecret;
    private String oauthUrl;
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
        clientId = props.getProperty("client.id");
        clientSecret = props.getProperty("client.secret");
        oauthUrl = props.getProperty("oauthURL");
        region = props.getProperty("region");
        poolId = props.getProperty("poolId");
    }

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
            //String redirectUri = "https://" + req.getServerName() + req.getContextPath() + "/auth";
            // Hardcoded local dev redirect URI
            String redirectUri = "http://localhost:8080/PersonalFoodReviewer/auth";


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
        } catch (Exception e) {
            logger.error("JWKS load error", e);
        }
    }

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
