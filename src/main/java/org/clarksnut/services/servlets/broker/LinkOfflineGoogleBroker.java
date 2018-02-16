package org.clarksnut.services.servlets.broker;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.clarksnut.models.*;
import org.jberet.support._private.SupportMessages;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@WebServlet("/api/auth/broker/google/endpoint")
public class LinkOfflineGoogleBroker extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LinkOfflineGoogleBroker.class);

    public static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    @Inject
    @ConfigurationValue("clarksnut.broker.vendor.google.clientId")
    private Optional<String> clarksnutGoogleBrokerClientId;

    @Inject
    @ConfigurationValue("clarksnut.broker.vendor.google.clientSecret")
    private Optional<String> clarksnutGoogleBrokerClientSecret;

    @Inject
    @ConfigurationValue("clarksnut.broker.vendor.google.redirectUris")
    private Optional<String> clarksnutGoogleBrokerRedirectUris;

    @Inject
    private UserProvider userProvider;

    @Inject
    private BrokerProvider brokerProvider;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!clarksnutGoogleBrokerClientId.isPresent() || !clarksnutGoogleBrokerClientSecret.isPresent()) {
            throw new ServletException("Additional Google Broker has not been configured");
        }

        String redirect = req.getParameter("redirect");
        if (redirect != null) {
            // Validate redirect
            List<String> validRedirectUris = Arrays.asList(clarksnutGoogleBrokerRedirectUris.orElse("").trim().split(","));
            if (!validRedirectUris.contains(redirect)) {
                redirect = redirect.substring(0, redirect.lastIndexOf("?")) + "?error=access_denied";
                resp.sendRedirect(redirect);
                return;
            }

            // State
            String state = UUID.randomUUID().toString();

            req.getSession().setAttribute("state", state);
            req.getSession().setAttribute("redirect", redirect);

            StringBuilder authRedirect = new StringBuilder(AUTH_URL)
                    .append("?client_id=").append(clarksnutGoogleBrokerClientId.get())
                    .append("&redirect_uri=").append(req.getRequestURL().toString())
                    .append("&scope=").append("openid profile email https://www.googleapis.com/auth/gmail.readonly")
                    .append("&access_type=").append("offline")
                    .append("&include_granted_scopes=").append("true")
                    .append("&state=").append(state)
                    .append("&response_type=").append("code");

            resp.sendRedirect(authRedirect.toString());
            return;
        } else {
            String authCode = req.getParameter("code");
            String authState = req.getParameter("state");

            if (authCode == null || authState == null) {
                throw new ServletException("Invalid target request");
            }

            String sessionRedirect = (String) req.getSession().getAttribute("redirect");
            String sessionState = (String) req.getSession().getAttribute("state");

            if (authState.equals(sessionState)) {
                AccessTokenResponse token = exchangeToken(authCode, req.getRequestURL().toString());
                if (token == null) {
                    sessionRedirect = sessionRedirect.substring(0, sessionRedirect.lastIndexOf("?")) + "?error=already_registered";
                    resp.sendRedirect(sessionRedirect);
                    return;
                }

                KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) req.getUserPrincipal();
                AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
                String username = accessToken.getPreferredUsername();

                saveToken(username, token);
                resp.sendRedirect(sessionRedirect);
                return;
            } else {
                sessionRedirect = sessionRedirect.substring(0, sessionRedirect.lastIndexOf("?")) + "?error=access_denied";
                resp.sendRedirect(sessionRedirect);
                return;
            }
        }
    }

    private AccessTokenResponse exchangeToken(String code, String redirectUri) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target("https://www.googleapis.com/oauth2/v4/token");

        Form form = new Form();
        form.param("code", code);
        form.param("client_id", clarksnutGoogleBrokerClientId.get());
        form.param("client_secret", clarksnutGoogleBrokerClientSecret.get());
        form.param("redirect_uri", redirectUri);
        form.param("grant_type", "authorization_code");

        final Entity<Form> entity = Entity.form(form);
        final Response response = target.request().post(entity);

        final Response.Status.Family statusFamily = response.getStatusInfo().getFamily();
        if (statusFamily == Response.Status.Family.CLIENT_ERROR || statusFamily == Response.Status.Family.SERVER_ERROR) {
            throw SupportMessages.MESSAGES.restApiFailure(response.getStatus(), response.getStatusInfo().getReasonPhrase(), response.getEntity());
        }

        AccessTokenResponse token = response.readEntity(AccessTokenResponse.class);
        client.close();

        return token.getRefreshToken() != null ? token : null;
    }

    private BrokerModel saveToken(String username, AccessTokenResponse token) throws JsonProcessingException {
        DecodedJWT jwt = JWT.decode(token.getIdToken());
        String email = jwt.getClaim("email").asString();

        UserModel user = userProvider.getUserByUsername(username);
        BrokerModel broker = brokerProvider.getBrokerByEmail(email);
        if (broker == null) {
            broker = brokerProvider.addBroker(user, BrokerType.GOOGLE, email);
        }

        ObjectMapper mapper = new ObjectMapper();
        broker.setToken(mapper.writeValueAsString(token));
        broker.setUser(user);

        return broker;
    }

}
