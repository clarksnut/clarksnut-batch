package org.clarksnut.models;

public interface UserModel {

    String getId();
    String getIdentityID();
    String getProviderType();
    String getUsername();

    String getOfflineToken();
    void setOfflineToken(String offlineToken);

}
