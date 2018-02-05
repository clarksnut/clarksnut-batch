package org.clarksnut.services.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import org.jboss.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebServlet("/api/authorize_offline")
public class OAuth2LinkUserOfflineRefreshToken extends AbstractAuthorizationCodeServlet {

    private static final Logger logger = Logger.getLogger(OAuth2LinkUserOfflineRefreshToken.class);

    public static final String CALLBACK = "/api/authorize_offline_callback";

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        return OAuth2Utils.buildRedirectURL(req, CALLBACK, redirect);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return OAuth2Utils.getAuthorizationCodeFlow(Constants.SCOPES);
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }

}
