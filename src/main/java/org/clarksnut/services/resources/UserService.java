package org.clarksnut.services.resources;

import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation getCurrentUser(@Context final HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();

        String kcUserID = principal.getName();
        String kcUsername = accessToken.getPreferredUsername();

        // Get user from DB
        UserModel user = this.userProvider.getUserByIdentityID(kcUserID);
        if (user == null) {
            user = this.userProvider.addUser(kcUserID, "kc", kcUsername);
        }

        // Result
        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

}