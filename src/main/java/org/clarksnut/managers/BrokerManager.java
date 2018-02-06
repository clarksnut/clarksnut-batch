package org.clarksnut.managers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.clarksnut.models.BrokerType;
import org.clarksnut.representations.idm.TokenRepresentation;
import org.clarksnut.services.KeycloakDeploymentConfig;
import org.keycloak.adapters.KeycloakDeployment;

import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RequestScoped
public class BrokerManager {

    /**
     * @param refreshToken refresh token
     * @return List of brokers that the token is able to read.
     * e.g [{google, email.google.com}, {microsoft, email.outlook.com}]
     */
    public Set<BrokerModel> getLinkedBrokers(String refreshToken) throws IOException {
        Set<BrokerModel> result = new HashSet<>();
        for (BrokerType broker : BrokerType.values()) {
            TokenRepresentation token = getBrokerToken(broker.getAlias(), refreshToken);
            if (token != null) {
                DecodedJWT jwt = JWT.decode(token.getId_token());
                result.add(new BrokerModel(broker, jwt.getClaim("email").asString()));
            }
        }
        return result;
    }

    private TokenRepresentation getBrokerToken(String broker, String refreshToken) throws IOException {
        KeycloakDeployment keycloakDeployment = KeycloakDeploymentConfig.getInstance().getDeployment();
        String authServer = keycloakDeployment.getAuthServerBaseUrl();
        String realmName = keycloakDeployment.getRealm();

        Credential credential = getCredential().setRefreshToken(refreshToken);
        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);

        GenericUrl url = new GenericUrl(authServer + "/realms/" + realmName + "/broker/" + broker + "/token");
        HttpResponse execute = requestFactory.buildGetRequest(url).execute();

        TokenRepresentation result = null;
        if (execute.isSuccessStatusCode()) {
            String response = execute.parseAsString();
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, TokenRepresentation.class);
        }
        execute.disconnect();
        return result;
    }

    public static Credential getCredential() {
        KeycloakDeploymentConfig kcDeploymentConfig = KeycloakDeploymentConfig.getInstance();

        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setTokenServerUrl(new GenericUrl(kcDeploymentConfig.getTokenUrl()))
                .setClientAuthentication(new BasicAuthentication(kcDeploymentConfig.getClientID(), kcDeploymentConfig.getClientSecret()))
                .build();
    }

}
