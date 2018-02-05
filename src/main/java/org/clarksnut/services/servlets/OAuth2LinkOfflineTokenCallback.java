package org.clarksnut.services.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import org.clarksnut.core.IStorage;
import org.clarksnut.models.UserProvider;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/authorize_offline_callback")
public class OAuth2LinkOfflineTokenCallback extends AbstractAuthorizationCodeCallbackServlet {

    private static final Logger logger = Logger.getLogger(OAuth2LinkOfflineTokenCallback.class);

    @Inject
    private IStorage storage;

    @Inject
    private UserProvider userProvider;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);

        String accessToken = credential.getAccessToken();
        /*DecodedJWT decodedJWT = JWT.decode(credential.getAccessToken());
        String identityID = decodedJWT.getClaim("userID").asString();

        try {
            storage.beginTx();

            UserModel user = this.userProvider.getUserByIdentityID(kcUserID);
            if (user == null) {
                user = this.userProvider.addUser(kcUserID, "kc", kcUsername);
            }

            UserBean bean = new UserBean();
            bean.setIdentityID(identityID);
            bean.setOfflineToken(credential.getRefreshToken());
            bean.setRegistrationComplete(true);
            userProvider.updateUser(bean);

            storage.commitTx();
        } catch (StorageException e) {
            storage.rollbackTx();
            throw new SystemErrorException(e);
        }*/

        resp.sendRedirect(redirect);
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        resp.sendRedirect(redirect + "?error=could not get token");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        String redirect = OAuth2Utils.getRedirect(req);
        return OAuth2Utils.buildRedirectURL(req, OAuth2LinkOfflineToken.CALLBACK, redirect);
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
