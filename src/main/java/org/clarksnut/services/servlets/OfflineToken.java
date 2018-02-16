package org.clarksnut.services.servlets;

import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.util.TokenUtil;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Transactional
@WebServlet("/api/auth/authorize_batch/*")
public class OfflineToken extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OfflineToken.class);

    public static final String CALLBACK = "/api/auth/authorize_batch/login?scope=offline_access";

    @Inject
    private UserProvider userProvider;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirect = req.getParameter("redirect");
        if (redirect == null) {
            throw new ServletException("Redirect parameter was not found.");
        }

        if (!req.getRequestURI().endsWith("/login")) {
            resp.sendRedirect(CALLBACK + "&redirect=" + redirect);
        } else {
            String refreshToken;
            try {
                RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
                refreshToken = ctx.getRefreshToken();

                RefreshToken refreshTokenDecoded = TokenUtil.getRefreshToken(refreshToken);
                Boolean isOfflineToken = refreshTokenDecoded.getType().equals(TokenUtil.TOKEN_TYPE_OFFLINE);
                if (!isOfflineToken) {
                    throw new ServletException("Token exchanged is not offline");
                }
            } catch (JWSInputException e) {
                throw new ServletException(e);
            }


            KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) req.getUserPrincipal();
            AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
            String username = accessToken.getPreferredUsername();

            UserModel user = userProvider.getUserByUsername(username);
            user.setToken(refreshToken);

            resp.sendRedirect(redirect);
        }
    }

}
