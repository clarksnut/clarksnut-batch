package org.clarksnut.models;

import java.util.Date;

public interface BrokerModel {

    String getId();

    BrokerType getType();

    String getEmail();

    String getToken();
    void setToken(String token);

    Date getLastTimeSynchronized();
    void setLastTimeSynchronized(Date lastTimeSynchronized);

    UserModel getUser();
    void setUser(UserModel user);

    Date getCreatedAt();
    Date getUpdatedAt();
}
