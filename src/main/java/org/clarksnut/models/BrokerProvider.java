package org.clarksnut.models;

import java.util.List;

public interface BrokerProvider {

    BrokerModel addBroker(UserModel user, BrokerType type, String email);

    BrokerModel getBroker(String id);

    BrokerModel getBrokerByEmail(String email);

    List<BrokerModel> getBrokers(UserModel user);

}
