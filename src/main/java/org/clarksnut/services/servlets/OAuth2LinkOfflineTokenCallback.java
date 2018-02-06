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
@WebServlet("/api/auth/authorize_offline_callback")
public class OAuth2LinkOfflineTokenCallback extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OAuth2LinkOfflineTokenCallback.class);

    @Inject
    private UserProvider userProvider;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirect = req.getParameter("redirect");

        // Token
        String refreshToken;
        RefreshToken refreshTokenDecoded;
        try {
            RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
            refreshToken = ctx.getRefreshToken();

            refreshTokenDecoded = TokenUtil.getRefreshToken(refreshToken);
            Boolean isOfflineToken = refreshTokenDecoded.getType().equals(TokenUtil.TOKEN_TYPE_OFFLINE);
            if (!isOfflineToken) {
                throw new ServletException("Token obtained is not offline");
            }
        } catch (JWSInputException e) {
            throw new ServletException(e);
        }

        // User
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) req.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String kcUserID = principal.getName();
        String kcUsername = accessToken.getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(kcUsername);
        if (user == null) {
            user = userProvider.addUser(kcUserID, "kc", kcUsername);
        }
        user.setOfflineToken(refreshToken);

        // Result
        resp.sendRedirect(redirect);
    }

}
