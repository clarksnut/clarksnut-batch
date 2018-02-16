package org.clarksnut.mail.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.Clock;
import org.clarksnut.representations.idm.TokenRepresentation;
import org.jboss.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeycloakBrokerHttpInterceptor implements HttpExecuteInterceptor, HttpUnsuccessfulResponseHandler {

    private static final Logger logger = Logger.getLogger(KeycloakBrokerHttpInterceptor.class);

    private final String authServerUrl;
    private final String realm;
    private final String broker;

    private final Credential credential;
    private final Lock lock = new ReentrantLock();

    private String keycloakAccessToken;
    private Long keycloakExpirationTimeMilliseconds;

    /**
     * Clock used to provide the currentMillis.
     */
    private final Clock clock = Clock.SYSTEM;

    public KeycloakBrokerHttpInterceptor(String authServerUrl, String realm, String broker, Credential credential) {
        this.authServerUrl = authServerUrl;
        this.realm = realm;
        this.broker = broker;
        this.credential = credential;
    }

    @Override
    public void intercept(HttpRequest request) throws IOException {
        String oldAccessToken = credential.getAccessToken();
        credential.intercept(request); // Refresh keycloak token
        String newAccessToken = credential.getAccessToken();

        // Refresh google token
        lock.lock();
        try {
            Long keycloakExpiresIn = getKeycloakExpiresInSeconds();
            if (keycloakAccessToken == null || !oldAccessToken.equals(newAccessToken) || keycloakExpiresIn != null && keycloakExpiresIn <= 60) {
                refreshKeycloakToken();
                if (keycloakAccessToken == null) {
                    // nothing we can do without an access token
                    logger.error("Nothing we can do without an access Google token (null)");
                    return;
                }
            }
            credential.getMethod().intercept(request, keycloakAccessToken);
        } finally {
            lock.unlock();
        }
    }

    private boolean refreshKeycloakToken() throws IOException {
        lock.lock();
        try {
            try {
                TokenRepresentation tokenResponse = executeKeycloakRefreshToken();
                if (tokenResponse != null) {
                    setFromKeycloakTokenResponse(tokenResponse);
                    return true;
                }
            } catch (TokenResponseException e) {
                logger.error("Error refreshing keycloak token status:" + e.getStatusCode() + " message:" + e.getMessage());

                boolean statusCode4xx = 400 <= e.getStatusCode() && e.getStatusCode() < 500;
                // check if it is a normal error response
                if (e.getDetails() != null && statusCode4xx) {
                    // We were unable to get a new access token (e.g. it may have been revoked), we must now
                    // indicate that our current token is invalid.
                    setKeycloakAccessToken(null);
                    setKeycloakExpiresInSeconds(null);
                }
                if (statusCode4xx) {
                    throw e;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private TokenRepresentation executeKeycloakRefreshToken() throws IOException {
        if (credential.getAccessToken() == null) {
            return null;
        }
        ClientRequestFilter authFilter = requestContext -> {
            requestContext.getHeaders().add(HttpHeaders.ACCEPT, "application/json");
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + credential.getAccessToken());
        };

        Client client = ClientBuilder.newBuilder().register(authFilter).build();
        WebTarget target = client.target(getIdentityProviderTokenUrl(broker));

        Response response = target.request().get();
        return response.readEntity(TokenRepresentation.class);
    }

    private String getIdentityProviderTokenUrl(String broker) {
        return authServerUrl + "/realms/" + realm + "/broker/" + broker + "/token";
    }

    private void setFromKeycloakTokenResponse(TokenRepresentation tokenResponse) {
        setKeycloakAccessToken(tokenResponse.getAccess_token());
        setKeycloakExpiresInSeconds(tokenResponse.getExpires_in());
    }

    private void setKeycloakAccessToken(String accessToken) {
        lock.lock();
        try {
            this.keycloakAccessToken = accessToken;
        } finally {
            lock.unlock();
        }
    }

    private void setKeycloakExpiresInSeconds(Long expiresIn) {
        setKeycloakExpirationTimeMilliseconds(expiresIn == null ? null : clock.currentTimeMillis() + expiresIn * 1000);
    }

    private void setKeycloakExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
        lock.lock();
        try {
            this.keycloakExpirationTimeMilliseconds = expirationTimeMilliseconds;
        } finally {
            lock.unlock();
        }
    }

    public Long getKeycloakExpiresInSeconds() {
        lock.lock();
        try {
            if (keycloakExpirationTimeMilliseconds == null) {
                return null;
            }
            return (keycloakExpirationTimeMilliseconds - clock.currentTimeMillis()) / 1000;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry) throws IOException {
        return credential.handleResponse(request, response, supportsRetry);
    }
}
