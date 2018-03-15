package org.clarksnut.models;

import java.util.Date;
import java.util.List;

public interface UserModel {

    String getId();

    String getIdentityID();
    void setIdentityID(String identityID);

    String getProvider();

    String getUsername();

    String getToken();
    void setToken(String token);

    Date getCreatedAt();

    List<BrokerModel> getLinkedBrokers();
}
