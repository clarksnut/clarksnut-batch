package org.clarksnut.services.servlets;

import org.clarksnut.services.KeycloakDeploymentConfig;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.AccessToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@WebServlet("/api/auth/broker/google/link")
public class LinkGoogleBroker extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LinkGoogleBroker.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirect = req.getParameter("redirect");
        if (redirect == null) {
            logger.error("Could not link without redirect parameter");
            throw new ServletException("Redirect parameter was not found.");
        }

        KeycloakSecurityContext session = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
        AccessToken token = session.getToken();
        String clientId = token.getIssuedFor();
        String nonce = UUID.randomUUID().toString();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String input = nonce + token.getSessionState() + clientId + "google";
        byte[] check = md.digest(input.getBytes(StandardCharsets.UTF_8));
        String hash = Base64Url.encode(check);
        req.getSession().setAttribute("hash", hash);

        KeycloakDeploymentConfig instance = KeycloakDeploymentConfig.getInstance();
        String accountLinkUrl = KeycloakUriBuilder.fromUri(instance.getDeployment().getAuthServerBaseUrl())
                .path("/realms/{realm}/broker/{provider}/link")
                .queryParam("nonce", nonce)
                .queryParam("hash", hash)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirect).build(instance.getDeployment().getRealm(), "google").toString();

        resp.sendRedirect(accountLinkUrl);
    }

}
