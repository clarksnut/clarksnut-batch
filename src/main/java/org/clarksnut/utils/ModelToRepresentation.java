package org.clarksnut.utils;

import org.clarksnut.models.BrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.representations.idm.BrokerRepresentation;
import org.clarksnut.representations.idm.GenericLinksRepresentation;
import org.clarksnut.representations.idm.UserAttributesRepresentation;
import org.clarksnut.representations.idm.UserRepresentation;
import org.clarksnut.services.resources.UsersService;

import javax.ejb.Stateless;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ModelToRepresentation {

    public UserRepresentation.Data toRepresentation(UserModel model, UriInfo uriInfo) {
        UserRepresentation.Data rep = new UserRepresentation.Data();

        rep.setId(model.getIdentityID());
        rep.setType("identities");

        // Links
        GenericLinksRepresentation links = new GenericLinksRepresentation();
        URI self = uriInfo.getBaseUriBuilder()
                .path(UsersService.class)
                .path(UsersService.class, "getUser")
                .build(model.getIdentityID());
        links.setSelf(self.toString());

        rep.setLinks(links);

        // Attributes
        UserAttributesRepresentation attributes = new UserAttributesRepresentation();
        rep.setAttributes(attributes);

        attributes.setUserID(model.getId());
        attributes.setIdentityID(model.getIdentityID());
        attributes.setProviderType(model.getProvider());
        attributes.setUsername(model.getUsername());
        attributes.setCreatedAt(model.getCreatedAt());

        // Brokers
        List<BrokerRepresentation.Data> brokers = model.getLinkedBrokers().stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList());
        attributes.setBrokers(brokers);

        return rep;
    }

    public BrokerRepresentation.Data toRepresentation(BrokerModel model) {
        BrokerRepresentation.Data rep = new BrokerRepresentation.Data();

        rep.setId(model.getId());
        rep.setType("brokers");

        // Attributes
        BrokerRepresentation.Attributes attributes = new BrokerRepresentation.Attributes();
        rep.setAttributes(attributes);

        attributes.setType(model.getType().getAlias());
        attributes.setEmail(model.getEmail());
        attributes.setToken(model.getToken() != null ? "***" : null);
        attributes.setEnabled(model.isEnabled());
        attributes.setCreatedAt(model.getCreatedAt());

        return rep;
    }

}
