package org.clarksnut.models;

import java.util.Date;

public interface BrokerModel {

    String getId();

    BrokerType getType();

    String getEmail();

    Date getLastTimeSynchronized();

    boolean isEnabled();

    UserModel getUser();
}
