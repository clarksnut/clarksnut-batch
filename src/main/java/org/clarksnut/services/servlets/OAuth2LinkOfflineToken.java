package org.clarksnut.services.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import org.jboss.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/authorize_offline")
public class OAuth2LinkOfflineToken extends AbstractAuthorizationCodeServlet {

    private static final Logger logger = Logger.getLogger(OAuth2LinkOfflineToken.class);

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

    @Override
    protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException {
        authorizationUrl.setState("xyz");
        super.onAuthorization(req, resp, authorizationUrl);
    }
}
