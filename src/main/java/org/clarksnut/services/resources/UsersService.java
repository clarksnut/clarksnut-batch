package org.clarksnut.services.resources;

import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.representations.idm.UserAttributesRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.resources.utils.PATCH;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
public class UsersService {

    private static final Logger logger = Logger.getLogger(UsersService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private UserProvider userProvider;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    private UserModel getUser(HttpServletRequest httpServletRequest) {
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) httpServletRequest.getUserPrincipal();
        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        String username = accessToken.getPreferredUsername();

        UserModel user = userProvider.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    private UserModel getUserByIdentityID(String identityID) {
        UserModel user = userProvider.getUserByIdentityID(identityID);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @GET
    @Path("{identityID}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation getUser(@PathParam("identityID") String identityID) {
        UserModel user = getUserByIdentityID(identityID);
        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
    }

//    @PATCH
//    @Produces(MediaType.APPLICATION_JSON)
//    public UserRepresentation currentUser(@Context final HttpServletRequest httpServletRequest, final UserRepresentation userRepresentation) {
//        UserModel user = getUser(httpServletRequest);
//        UserAttributesRepresentation userAttributesRepresentation = userRepresentation.getData().getAttributes();
//
//        if (userAttributesRepresentation != null) {
//            // Is registration completed
//            Boolean registrationCompleted = userAttributesRepresentation.getRegistrationCompleted();
//            if (registrationCompleted != null) {
//                user.setRegistrationComplete(registrationCompleted);
//            }
//        }
//
//        return modelToRepresentation.toRepresentation(user, uriInfo).toUserRepresentation();
//    }

}