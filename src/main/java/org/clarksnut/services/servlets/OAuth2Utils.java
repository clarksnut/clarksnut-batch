package org.clarksnut.services.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.clarksnut.services.KeycloakDeploymentConfig;
import org.jboss.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class OAuth2Utils {

    private static final Logger logger = Logger.getLogger(OAuth2Utils.class);

    public static String getRedirect(HttpServletRequest req) throws ServletException {
        String redirect = req.getParameter(Constants.REDIRECT_REQUEST_ATTRIBUTE_NAME);
        if (redirect == null) {
            throw new ServletException("Request attribute[" + Constants.REDIRECT_REQUEST_ATTRIBUTE_NAME + "] required");
        }
        return redirect;
    }

    public static String buildRedirectURL(HttpServletRequest req, String callback, String redirect) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(callback);

        String redirect_url = url.build() + "?redirect=" + redirect;
        logger.debug("redirect_url:" + redirect_url);
        return redirect_url;
    }

    public static AuthorizationCodeFlow getAuthorizationCodeFlow(List<String> scopes) {
        return getAuthorizationCodeFlowBuilder()
                .setScopes(scopes)
                .build();
    }

    public static AuthorizationCodeFlow.Builder getAuthorizationCodeFlowBuilder() {
        KeycloakDeploymentConfig kcDeploymentConfig = KeycloakDeploymentConfig.getInstance();

        return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new JacksonFactory(),
                new GenericUrl(kcDeploymentConfig.getTokenUrl()),
                new BasicAuthentication(kcDeploymentConfig.getClientID(), kcDeploymentConfig.getClientSecret()),
                kcDeploymentConfig.getClientID(),
                kcDeploymentConfig.getAuthorizationUrl());
    }

}
